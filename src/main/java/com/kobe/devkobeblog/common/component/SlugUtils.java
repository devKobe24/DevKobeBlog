package com.kobe.devkobeblog.common.component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern DATE_PREFIX = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2})-");

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
        if (input == null) return "";
        String s = input.trim();
        if (s.isEmpty()) return "";

        s = Normalizer.normalize(s, Normalizer.Form.NFKC);
        s = s.replaceAll("[\\s_]+", "-");
        s = s.toLowerCase(Locale.ROOT);
        s = s.replaceAll("[^a-z0-9\\-]", "");
        s = s.replaceAll("\\-+", "-");
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
        if (filename == null) return "";
        String name = filename.trim();
        if (name.isEmpty()) return "";

        name = name.replaceFirst("(?i)\\.(md|markdown)$", "");
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
        if (path == null) {
            return "";
        }

        String normalized = path.replace("\\", "/");

        // collapse duplicate slashes
        normalized = normalized.replaceAll("/+", "/");

        // trim leading/trailing slashes
        normalized = normalized.replaceAll("^/+|/+$", "");

        return normalized;
    }

    // ✅ 추가: "2026-02-13-virtual-memory.md" -> "2026-02-13"
    public static String extractDatePrefix(String filename) {
        if (filename == null) return "";
        String s = filename.trim();
        if (s.isEmpty()) return "";

        Matcher m = DATE_PREFIX.matcher(s);
        return m.find() ? m.group(1) : "";
    }
}
