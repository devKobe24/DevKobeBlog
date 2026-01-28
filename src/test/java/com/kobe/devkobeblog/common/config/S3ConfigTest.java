package com.kobe.devkobeblog.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName    : com.kobe.devkobeblog.common.config
 * fileName       : S3ConfigTest
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 26.        kobe / Minsung Kang       최초 생성
 */

@SpringBootTest(
        classes = S3Config.class,
        properties = {
                "cloud.aws.credentials.access-key=test-access-key",
                "cloud.aws.credentials.secret-key=test-secret-key",
                "cloud.aws.region.static=ap-northeast-2"
        }
)
class S3ConfigTest {

    @Autowired
    private S3Client s3Client;

    @Test
    @DisplayName("S3Client 빈이 정상적으로 생성되고 리전 설정이 올바른지 확인한다.")
    void s3Client_Should_Be_Loaded_With_Correct_Region() {
        // given & when (Context Loading)

        // then
        // 1. 빈이 null이 아닌지 확인
        assertThat(s3Client).isNotNull();

        // 2. 설정한 리전(ap-northeast-2)이 잘 들어갔는지 확인
        // AWS SDK v2에서는 serviceClientConfiguration()을 통해 설정을 조회할 수 있습니다.
        assertThat(s3Client.serviceClientConfiguration().region().id())
                .isEqualTo("ap-northeast-2");
    }

}