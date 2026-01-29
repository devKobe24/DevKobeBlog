package com.kobe.devkobeblog.common.component;

import com.kobe.devkobeblog.post.dto.PostParseResult;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * packageName    : com.kobe.devkobeblog.common.component
 * fileName       : MarkdownProcessor
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 26.
 * description    : flexmark-java의 강력한 기능인 AST(추상 구문 트리) 순회를 사용하여, 정규식보다 훨씬 안전하고 세련되게 이미지를 처리합니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 26.        kobe / Minsung Kang       최초 생성
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class MarkdownProcessor {

    private final S3Uploader s3Uploader;

    // Flexmark 설정: YAML Front Matter, 테이블, GFM 태스크 리스트(체크박스)
    private static final MutableDataSet OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, Arrays.asList(
                    YamlFrontMatterExtension.create(),
                    TablesExtension.create(),
                    TaskListExtension.create(),
                    AnchorLinkExtension.create()
            ))
            .set(AnchorLinkExtension.ANCHORLINKS_SET_ID, true) // h 태그에 id 속성 부여
            .set(AnchorLinkExtension.ANCHORLINKS_ANCHOR_CLASS, "anchor-links") // 링크 클래스명
            .set(AnchorLinkExtension.ANCHORLINKS_TEXT_SUFFIX, "") // 텍스트 뒤에 붙을 기호 (없음으로 설정)
            .set(TablesExtension.COLUMN_SPANS, false)
            .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
            .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
            .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true);
    private static final Parser PARSER = Parser.builder(OPTIONS).build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();

    /**
     * @Param markdownFile 처리할 마크다운 파일 (.md)
     * @Param gitRootPath Git 저장소의 최상위 루트 경로 (추가됨)
     */
    public PostParseResult process(Path markdownFile, Path gitRootPath) throws IOException {
        // 1. 파일 내용 읽기
        String markdownContent = Files.readString(markdownFile);

        // 2. AST(Abstract Syntax Tree) 파싱
        Node document = PARSER.parse(markdownContent);

        // 3. Front Matter (메타데이터) 추출
        AbstractYamlFrontMatterVisitor visitor = new AbstractYamlFrontMatterVisitor();
        visitor.visit(document);
        Map<String, List<String>> metadata = visitor.getData();

        // 4. 이미지 처리 (핵심 로직: Local Path -> S3 URL 치환)
        replaceImages(document, markdownFile.getParent(), gitRootPath);

        // 5. HTML 렌더링
        String html = RENDERER.render(document);

        // 6. 결과 DTO 생성
        return createResult(metadata, html, markdownFile.getParent(), gitRootPath);
    }

    private void replaceImages(Node document, Path currentMdFileDir, Path gitRootPath) {
        // AST를 순회하며 Image 노드만 찾습니다
        for (Node node : document.getDescendants()) {
            if (node instanceof Image) {
                Image image = (Image) node;
                String imageUrl = image.getUrl().toString();

                // 웹 URL(http)이 아니고 로컬 경로인 경우에만 처리
                if (!imageUrl.startsWith("http") && !imageUrl.startsWith("//")) {
                    try {
                        Path imagePath;
                        if (imageUrl.startsWith("/")) {
                            // "/assets/..." -> Git Root 기준
                            imagePath = gitRootPath.resolve(imageUrl.substring(1));
                        } else {
                            // "./img/..." -> 현재 MD 파일 기준
                            imagePath = currentMdFileDir.resolve(imageUrl);
                        }

                        byte[] imageBytes = Files.readAllBytes(imagePath);
                        String filename = imagePath.getFileName().toString();
                        // S3 업로드
                        String s3Url = s3Uploader.upload(imageBytes, filename);

                        // AST 노드의 URL 속성을 S3 주소로 변경 (이후 랜더링 시 반영됨)
                        image.setUrl(BasedSequence.of(s3Url));

                        log.info("Image uploaded and replaced: {} -> {}", imageUrl, s3Url);
                    } catch (IOException e) {
                        log.error("Failed to upload image: " + imageUrl, e);
                        // 실패 시 예외를 던지거나, 로그를 남기고 원본 유지 (정책 결정 필요)
                    }
                }
            }
        }
    }

    private PostParseResult createResult(Map<String, List<String>> metadata, String html, Path currentMdFileDir, Path gitRootPath) {
        // 메타데이터 파싱 (없을 경우 기본값 처리)
        String title = getMetadataValue(metadata, "title", "Untitled");
        String dateStr = getMetadataValue(metadata, "date", LocalDate.now().toString());
        String publishedStr = getMetadataValue(metadata, "published", "true");

        // 1. Tags 처리 (List<String> 그대로 가져오기)
        List<String> tags = metadata.getOrDefault("tags", Collections.emptyList());

        // 2. Thumbnail 처리 (S3 업로드 포함)
        String rawThumbnailPath = getMetadataValue(metadata, "thumbnail", null);
        String thumbnailUrl = processThumbnail(rawThumbnailPath, currentMdFileDir, gitRootPath);

        // 날짜 포맷팅 (YYY-MM-DD 가정)
        LocalDateTime date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE).atStartOfDay();
        boolean isPublic = Boolean.parseBoolean(publishedStr);

        return new PostParseResult(title, html, date, isPublic, thumbnailUrl, tags);
    }

    /**
     * 썸네일 경로가 로컬 파일이면 S3에 업로드하고 URL을 반환합니다.
     * 웹 URL(http)이거나 null이면 그대로 반환합니다.
     */
    private String processThumbnail(String rawPath, Path currentMdFileDir, Path gitRootPath) {
        if (rawPath == null || rawPath.isEmpty()) {
            return null; // 썸네일 없음
        }
        if (rawPath.startsWith("http") || rawPath.startsWith("//")) {
            return rawPath; // 이미 웹 URL임
        }

        // 로컬 파일로 간주하고 업로드 시도
        try {
            Path imagePath;
            if (rawPath.startsWith("/")) {
                // "/assets/..." -> Git Root 기준
                imagePath = gitRootPath.resolve(rawPath.substring(1));
            } else {
                // "/./img/..." -> 현재 MD 파일 기준
                imagePath = currentMdFileDir.resolve(rawPath);
            }

            if (Files.exists(imagePath)) {
                byte[] imageBytes = Files.readAllBytes(imagePath);
                String filename = imagePath.getFileName().toString();
                return s3Uploader.upload(imageBytes, filename); // S3 URL 반환
            } else {
                log.warn("Thumbnail file not found at: {}", imagePath);
                return rawPath; // 파일이 없으면 원본 경로 반환 (혹은 null)
            }
        } catch (IOException e) {
            log.error("Failed to upload thumbnail: {}", rawPath, e);
            return rawPath;
        }
    }

    private String getMetadataValue(Map<String, List<String>> metadata, String key, String defaultValue) {
        if (metadata.containsKey(key) && !metadata.get(key).isEmpty()) {
            return metadata.get(key).get(0);
        }
        return defaultValue;
    }
}
