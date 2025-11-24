package com.junydev.spring.api.csv;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(BatchController.class)
public class BatchControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private BatchService batchService;

    @Test
    @DisplayName("CSV 파일 업로드 성공 테스트")
    void importUsersSuccessTest() {
        String csv = """
                name,email,gender
                홍길동,hong@example.com,Male
                김철수,kim@example.com,Male
                이영희,lee@example.com,Female
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "users.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8)
        );

        given(this.batchService.runBatch(any(MultipartFile.class))).willReturn(
                BatchStatus.COMPLETED
        );

        MvcTestResult result = this.mvc.post().uri("/api/batch/csv")
                .multipart().file(file)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.status").isEqualTo("success");
    }

    @Test
    @DisplayName("CSV 파일 업로드 실패 테스트")
    void importUsersFailTest() {
        String csv = """
                name,email,gender
                홍길동,hong@example.com,Male
                김철수,kim@example.com,Male
                이영희,lee@example.com,Female
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "users.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8)
        );

        given(this.batchService.runBatch(any(MultipartFile.class))).willReturn(
                BatchStatus.FAILED
        );

        MvcTestResult result = this.mvc.post().uri("/api/batch/csv")
                .multipart().file(file)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.status").isEqualTo("fail");
    }
}
