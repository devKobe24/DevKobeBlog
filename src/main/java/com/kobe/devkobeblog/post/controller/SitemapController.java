package com.kobe.devkobeblog.post.controller;

import com.kobe.devkobeblog.post.domain.Post;
import com.kobe.devkobeblog.post.domain.PostRepository;
import com.kobe.devkobeblog.post.domain.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 동적 sitemap.xml 생성 컨트롤러.
 * 요청 시점에 DB의 공개 게시글을 조회하여 XML을 생성하므로,
 * Webhook/Git sync 이후 자동으로 새 게시물이 반영됩니다.
 */
@RestController
@RequiredArgsConstructor
public class SitemapController {

    private static final DateTimeFormatter SITEMAP_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String SITEMAP_NS = "http://www.sitemaps.org/schemas/sitemap/0.9";

    private final PostRepository postRepository;

    @Value("${blog.site.url}")
    private String baseUrl;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap() {
        String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<urlset xmlns=\"").append(SITEMAP_NS).append("\">");

        // 메인 페이지
        xml.append("<url>");
        xml.append("<loc>").append(escapeXml(base)).append("/</loc>");
        xml.append("<changefreq>daily</changefreq>");
        xml.append("<priority>1.0</priority>");
        xml.append("</url>");

        // 공개 게시글 목록
        List<Post> posts = postRepository.findAllByStatusOrderByPublishedAtDesc(PostStatus.PUBLIC);
        for (Post post : posts) {
            xml.append("<url>");
            xml.append("<loc>").append(escapeXml(base)).append("/posts/").append(post.getId()).append("</loc>");
            if (post.getPublishedAt() != null) {
                xml.append("<lastmod>").append(post.getPublishedAt().format(SITEMAP_DATE)).append("</lastmod>");
            }
            xml.append("<changefreq>weekly</changefreq>");
            xml.append("<priority>0.8</priority>");
            xml.append("</url>");
        }

        xml.append("</urlset>");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/xml; charset=UTF-8"))
                .body(xml.toString());
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}