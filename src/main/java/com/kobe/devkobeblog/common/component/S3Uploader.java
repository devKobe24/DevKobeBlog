package com.kobe.devkobeblog.common.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * packageName    : com.kobe.devkobeblog.common.component
 * fileName       : S3Uploader
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 26.
 * description    : Markdown 파서에서 주입받아 사용
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 26.        kobe / Minsung Kang       최초 생성
 */

@Component
@RequiredArgsConstructor
public class S3Uploader {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // byte[] 데이터를 받아서 S3에 업로드하고 URL을 반환
    public String upload(byte[] fileData, String originalFileName) {
        // 파일명 중복 방지를 위한 UUID 생성
        String fileName = "images/" + UUID.randomUUID() + "_" + originalFileName;

        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType("image/png") // 필요 시 동적으로 변경
                .build(); // public-read 권한이 필요할 수 있음 (버킷 정책에 따라 다름)

        s3Client.putObject(putOb, RequestBody.fromByteBuffer(ByteBuffer.wrap(fileData)));

        // 업로드된 이미지 URL 반환 (Virtual Hosted Style_
        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucket, fileName);
    }
}
