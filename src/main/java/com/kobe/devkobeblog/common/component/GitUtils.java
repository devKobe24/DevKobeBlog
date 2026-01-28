package com.kobe.devkobeblog.common.component;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * packageName    : com.kobe.devkobeblog.common.component
 * fileName       : GitUtils
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 27.
 * description    : JGit 로직을 서비스에 직접 넣으면 코드가 지저분해지므로 별도의 컴포넌트로 분리함.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 27.        kobe / Minsung Kang       최초 생성
 */

@Slf4j
@Component
public class GitUtils {

    @Value("${blog.git.url}")
    private String gitUrl;

    @Value("${blog.git.local-path}")
    private String localPathStr;

    /**
     * Git 저장소를 동기화합니다.
     * 없으면 Clone, 있으면 Pull을 수행합니다.
     * @return 동기화된 로컬 저장소의 루트 경로
     */
    public Path sync() throws IOException, GitAPIException {
        File localDir = new File(localPathStr);

        if (isGitRepository(localDir)) {
            log.info("Executing Git Pull...");
            try (Git git = Git.open(localDir)) {
                git.pull().call();
            }
        } else {
            log.info("Executing Git Clone...");
            // 디렉토리가 지저분하면 삭제 후 다시 클론 (안전장치)
            if (localDir.exists()) {
                deleteDirectory(localDir);
            }
            Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(localDir)
                    .setBranch("main")
                    .call();
        }
        return Paths.get(localPathStr);
    }

    private boolean isGitRepository(File dir) {
        return new File(dir, ".git").exists();
    }

    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }
}
