package com.kobe.devkobeblog.common.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * packageName    : com.kobe.devkobeblog.common.component
 * fileName       : S3UploaderTest
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 26.        kobe / Minsung Kang       최초 생성
 */

// Spring Context를 띄우지 않고 Mockito만 사용하여 가볍고 빠르게 테스트합니다.
@ExtendWith(MockitoExtension.class)
class S3UploaderTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Uploader s3Uploader;

    private final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        // @Value("${...}") 필드는 Mockito가 주입해주지 못하므로,
        // ReflectionTestUtils를 사용하여 강제로 값을 주입합니다.
        ReflectionTestUtils.setField(s3Uploader, "bucket", BUCKET_NAME);
    }

    @Test
    @DisplayName("파일을 S3에 업로드하고 올바른 URL을 반환해야 합니다.")
    void upload_Should_Return_Correct_Url_And_Call_S3Client() {
        // given
        String originalFileName = "test-image.png";
        byte[] fileData = "Dummy File Content".getBytes();

        // when
        String resultUrl = s3Uploader.upload(fileData, originalFileName);

        // then
        // 1. 반환된 URL 포맷 검증
        // 예상: https://test-bucket.s3-ap-northeast-2.amazonaws.com/images/{UUID}_test-image.png
        assertThat(resultUrl)
                .startsWith("https://" + BUCKET_NAME + ".s3.ap-northeast-2.amazonaws.com/images/")
                .endsWith("_" + originalFileName);

        // 2. S3Client.putObject가 실제로 호출되었는지 검증
        // ArgumentCaptor를 사용하여 실제 호출될 때 넘겨진 파라미터를 낚아챕니다.
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);

        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));

        // 3. 호출 시 넘겨진 버킷 이름과 키 값 검증
        PutObjectRequest captureRequest = captor.getValue();
        assertThat(captureRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(captureRequest.contentType()).isEqualTo("image/png");
        assertThat(captureRequest.key()).contains("images/"); // 키 경로 확인
    }

}