package com.kobe.devkobeblog.post.controller;

import com.kobe.devkobeblog.post.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

/**
 * packageName    : com.kobe.devkobeblog.post.controller
 * fileName       : BlogController
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 28.        kobe / Minsung Kang       최초 생성
 */
@Controller
@RequiredArgsConstructor
public class BlogController {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    /**
     * 메인 페이지: 게시글 목록 조회 (검색 / 카테고리 / 태그 필터링 + 페이징)
     *
     * ✅ category 파라미터는 Category.slug 로 받는 전제
     * 예: /?category=network
     */
    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            Model model,
            @PageableDefault(size = 9, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Post> postPage;

        if (hasText(q)) {
            postPage = postRepository.findByTitleContainingIgnoreCaseAndStatus(
                    q.trim(),
                    PostStatus.PUBLIC,
                    pageable
            );
        } else if (hasText(category)) {
            // ✅ Category 엔티티를 먼저 조회할 필요 없이 slug로 바로 조회
            postPage = postRepository.findAllByCategory_SlugAndStatus(
                    category.trim(),
                    PostStatus.PUBLIC,
                    pageable
            );
        } else if (hasText(tag)) {
            postPage = postRepository.findAllByTagsNameAndStatus(
                    tag.trim(),
                    PostStatus.PUBLIC,
                    pageable
            );
        } else {
            postPage = postRepository.findAllByStatus(PostStatus.PUBLIC, pageable);
        }

        model.addAttribute("posts", postPage);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("tags", tagRepository.findAll());
        model.addAttribute("q", safe(q));
        model.addAttribute("category", safe(category));
        model.addAttribute("tag", safe(tag));

        return "index";
    }

    /**
     * ✅ 새 상세 페이지 URL: /posts/{categorySlug}/{slug}
     * - 외부 노출 URL
     * - PUBLIC만 허용
     */
    @GetMapping("/posts/{categorySlug}/{slug}")
    public String postBySlug(
            @PathVariable String categorySlug,
            @PathVariable String slug,
            Model model
    ) {
        Post post = postRepository
                .findByCategory_SlugAndSlugAndStatus(categorySlug, slug, PostStatus.PUBLIC)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));

        model.addAttribute("post", post);
        model.addAttribute("categories", categoryRepository.findAll());
        return "post";
    }

    /**
     * ✅ 기존 상세 URL 유지: /posts/{id}
     * - 숫자만 매칭되게 해서 slug URL과 충돌 방지
     * - 새 URL로 301(영구) 리다이렉트
     */
    @GetMapping("/posts/{id:\\d+}")
    public RedirectView postByIdRedirect(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 글입니다."));

        if (post.getStatus() != PostStatus.PUBLIC) {
            // 비공개/삭제글은 굳이 리다이렉트 시키지 않고 404/에러로 처리 추천
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "비공개 처리된 글입니다.");
        }

        // ✅ Post에 categorySlug가 없으므로 Category.slug 사용
        if (post.getCategory() == null || post.getCategory().getSlug() == null || post.getSlug() == null) {
            // 라우팅 정보가 불완전하면 404 처리(데이터 정합성 문제)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 글입니다.");
        }

        String target = "/posts/" + post.getCategory().getSlug() + "/" + post.getSlug();

        RedirectView redirectView = new RedirectView(target);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // 301
        return redirectView;
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
