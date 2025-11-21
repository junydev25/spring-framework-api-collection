package com.junydev.spring.api.board.controller;

import com.junydev.spring.api.board.internal.dto.PostStatDto;
import com.junydev.spring.api.board.internal.dto.StatType;
import com.junydev.spring.api.board.service.PostsStatisticsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@WebMvcTest(value = PostsStatisticsController.class)
public class PostsStatisticsControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private PostsStatisticsService postsStatisticsService;

    @Test
    @DisplayName("통계 확인")
    void getStats() {
        given(this.postsStatisticsService.getStats(List.of(StatType.VIEW), 1L)).willReturn(
                PostStatDto.builder()
                        .viewCounts(3L)
                        .likeCounts(0L)
                        .commentCounts(0L)
                        .build()
        );

        MvcTestResult result = this.mvc.get().uri("/api/posts/{id}/stat", 1L)
                .queryParam("stats", "view")
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.status").isEqualTo("success");
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data.viewCounts").isEqualTo(3);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data.likeCounts").isEqualTo(0);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data.commentCounts").isEqualTo(0);
    }

    @Test
    @DisplayName("통계 업데이트")
    void updateStats() {
        willDoNothing().given(this.postsStatisticsService).updateCounts(1L, StatType.VIEW, 1);

        MvcTestResult result = this.mvc.post()
                .uri("/api/posts/{id}/stat/{type}", 1L, "view")
                .queryParam("counts", "1")
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.status").isEqualTo("success");
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data").isNull();
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.message").isNull();
    }
}
