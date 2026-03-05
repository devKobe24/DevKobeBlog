package com.kobe.devkobeblog.common.component;

import java.text.Normalizer;
import java.util.Locale;

/**
 * packageName    : com.kobe.devkobeblog.common.component
 * fileName       : SlugUtils
 * author         : kobe / Minsung Kang
 * date           : 2026. 3. 5.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 3. 5.        kobe / Minsung Kang       최초 생성
 */
public final class SlugUtils {

    private SlugUtils() {
    }

    /**
     * Convert arbitrary text into a URL-friendly slug.
     * RULES:
     * - trim
     * - Unicode normalize (NFKC)
     * - whitespace/_ -> hyphen
     * - keep only [a-z0-9-]
     * - collapse multiple hyphens
     * - trim hyphens
     */
    public static String slugify(String input) {
        if (input == null) {
            return "";
        }

        String s = input.trim();
        if (s.isEmpty()) {
            return "";
        }

        // Normalize unicode to reduce weird variants
        s = Normalizer.normalize(s, Normalizer.Form.NFKC);

        // Replace spaces and underscore with hyphen
        s = s.replaceAll("[\\s_]+", "-");

        // Lowercase
        s = s.toLowerCase(Locale.ROOT);

        // Remove everything except a-z, 0-9, hyphen
        s = s.replaceAll("[^a-z0-9\\-]", "");

        // Collapse multiple hyphens
        s = s.replaceAll("\\-+", "-");

        // Trim leading/triling hyphens
        s = s.replaceAll("^\\-+|\\-+$", "");

        return s;
    }

    /**
     * Extract post slug from markdown filename.
     * Example:
     * - "2026-03-05-protocl.md" -> "protocol"
     * - "protocol.md" -> "protocol"
     * - "2026-03-05-Core-Summary-Of-Network-Basic.md-> "core-summary-of-network-basic"
     */
    public static String extractSlugFromFilename(String filename) {
        if (filename == null) {
            return "";
        }

        String name = filename.trim();
        if (name.isEmpty()) {
            return "";
        }

        // Remove extension (.md or .markdown)
        name = name.replaceFirst("(?i)\\.(md|markdown)$", "");

        // Remove date prefix: YYYY-MM-DD-
        name = name.replaceFirst("^\\d{4}-\\d{2}-\\d{2}-", "");

        return slugify(name);
    }

    /**
     * Extract category slug from a directory name (same as slugify, provided for readability).
     */
    public static String categorySlug(String categoryDirName) {
        return slugify(categoryDirName);
    }

    /**
     * Normalize file path separators to '/' for cross-platform consistency.
     */
    public static String normalizePath(String path) {
        return path == null ? "" : path.replace("\\", "/");
    }
}
