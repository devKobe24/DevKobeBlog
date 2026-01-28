package com.kobe.devkobeblog.post.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * packageName    : com.kobe.devkobeblog.post.dto
 * fileName       : PostParseResult
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 26.
 * description    : 파싱이 끝나면 DB에 저장될 데이터가 나옵니다. 이를 담을 레코드(Record)입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 26.        kobe / Minsung Kang       최초 생성
 */
public record PostParseResult(
        String title,
        String contentHtml,
        LocalDateTime date,
        boolean isPublic,
        String thumbnail,
        List<String> tags
) {}
