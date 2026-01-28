package com.kobe.devkobeblog.post.controller;

import com.kobe.devkobeblog.post.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
     * 메인 페이지: 게시글 목록 조회 (카테고리/태그 필터링 + 페이징)
     */
    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            Model model,
            @PageableDefault(size = 9, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Post> postPage;

        // 1. 카테고리 필터링
        if (category != null && !category.isBlank()) {
            Category cat = categoryRepository.findByName(category)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
            postPage = postRepository.findAllByCategoryAndStatus(cat, PostStatus.PUBLIC, pageable);
        }
        // 2. 태그 필터링
        else if (tag != null && !tag.isBlank()) {
            postPage = postRepository.findAllByTagsNameAndStatus(tag, PostStatus.PUBLIC, pageable);
        }
        // 3. 전체 조회
        else {
            postPage = postRepository.findAllByStatus(PostStatus.PUBLIC, pageable);
        }

        // 게시글 데이터
        model.addAttribute("posts", postPage);

        // 사이드바 구성을 위한 데이터 (모든 카테고리 및 태그)
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("tags", tagRepository.findAll());

        return "index";
    }

    /**
     * 상세 페이지: 게시글 내용 조회
     */
    @GetMapping("/posts/{id}")
    public String post(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));

        // 비공개 글이나 삭제된 글 접근 제어 로직 필요 시 추가
        if (post.getStatus() != PostStatus.PUBLIC) {
            throw new IllegalArgumentException("비공개 처리된 글입니다.");
        }

        model.addAttribute("post", post);

        // 상세 페이지에서도 사이드바나 네비게이션을 위해 카테고리 목록이 필요할 수 있음(선택 사항)
        model.addAttribute("categories", categoryRepository.findAll());

        return "post";
    }
}
