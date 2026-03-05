package com.kobe.devkobeblog.post.service;

import com.kobe.devkobeblog.common.component.GitUtils;
import com.kobe.devkobeblog.common.component.SlugUtils;
import com.kobe.devkobeblog.post.domain.Post;
import com.kobe.devkobeblog.post.domain.PostRepository;
import com.kobe.devkobeblog.post.domain.PostStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * packageName    : com.kobe.devkobeblog.post.service
 * fileName       : PostSyncService
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 27.        kobe / Minsung Kang       최초 생성
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class PostSyncService {

    private final GitUtils gitUtils;
    private final PostRepository postRepository;
    private final java.util.concurrent.locks.ReentrantLock syncLock = new java.util.concurrent.locks.ReentrantLock();
    private final PostSyncWorker postSyncWorker;

    @Async("syncTaskExecutor")
    public void syncPosts() {
        if (!syncLock.tryLock()) {
            log.warn("syncPosts is already running. Skip this run.");
            return;
        }

        boolean completed = false;
        Set<String> processedFilePaths = new HashSet<>();

        try {
            Path gitRoot = gitUtils.sync();
            Path postsDir = gitRoot.resolve("posts");

            if (Files.exists(postsDir)) {
                try (Stream<Path> paths = Files.walk(postsDir)) {
                    paths.filter(p -> p.toString().endsWith(".md"))
                            .forEach(filePath -> {
                                String rel = null;
                                try {
                                    rel = SlugUtils.normalizePath(gitRoot.relativize(filePath).toString());
                                    // ✅ 파일 1개 단위 트랜잭션(권장)
                                    postSyncWorker.processSinglePostTx(filePath, gitRoot);
                                    processedFilePaths.add(rel);

                                } catch (Exception e) {
                                    log.error("Failed to process file: {}", (rel != null ? rel : filePath), e);
                                    // 운영 안정성: 한 파일 실패로 전체 sync를 중단하지 않음
                                }
                            });
                }
            }

            completed = true;
            log.info("Post sync scan completed. processed={}", processedFilePaths.size());

        } catch (Exception e) {
            log.error("Failed to sync posts.", e);
        } finally {
            try {
                if (completed && !processedFilePaths.isEmpty()) {
                    // ✅ delete phase는 성공한 경우에만
                    handleDeletedPostsTx(processedFilePaths);
                    log.info("Post sync completed successfully.");
                } else {
                    log.warn("Post sync did NOT complete or processed is empty. Skipping delete phase.");
                }
            } finally {
                syncLock.unlock();
            }
        }
    }

    @Transactional
    protected void handleDeletedPostsTx(Set<String> processedFilePaths) {
        handleDeletedPosts(processedFilePaths);
    }

    private void handleDeletedPosts(Set<String> processedFilePaths) {
        // DB에는 있지만(DELETED 제외), 방금 처리된 목록에는 없는 글 조회
        List<Post> activePosts = postRepository.findAllByStatusNot(PostStatus.DELETED);

        for (Post post : activePosts) {
            String fp = SlugUtils.normalizePath(post.getFilePath());
            if (!processedFilePaths.contains(fp)) {
                log.info("Marking post as DELETED: {}", post.getTitle());
                post.delete(); // Soft Delete 수행
            }
        }
    }
}
