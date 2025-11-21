package com.junydev.spring.api.board.service;

import com.junydev.spring.api.board.internal.dto.PostStatDto;
import com.junydev.spring.api.board.internal.dto.StatType;
import com.junydev.spring.api.board.internal.entity.PostsStatistics;
import com.junydev.spring.api.board.repository.PostsStatisticsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostsStatisticsServiceTest {

    @Mock
    private PostsStatisticsRepository postsStatisticsRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PostsStatisticsService postsStatisticsService;

    @Test
    @DisplayName("통계 없데이트 - RabbitMQ 메시지 전송 확인(VIEW)")
    void updateViewCountsWithSendMessageToRabbitMQ() {
        Long postId = 1L;
        StatType statType = StatType.VIEW;
        int counts = 5;

        this.postsStatisticsService.updateCounts(postId, statType, counts);

        verify(this.rabbitTemplate, times(1))
                .convertAndSend(
                        eq("post.stats.exchange"),
                        eq("VIEW"),
                        eq(Map.of("id", postId, "counts", counts))
        );
    }

    @Test
    @DisplayName("통계 없데이트 - RabbitMQ 메시지 전송 확인(LIKE)")
    void updateLikeCountsWithSendMessageToRabbitMQ() {
        Long postId = 1L;
        StatType statType = StatType.LIKE;
        int counts = 5;

        this.postsStatisticsService.updateCounts(postId, statType, counts);

        verify(this.rabbitTemplate, times(1))
                .convertAndSend(
                        eq("post.stats.exchange"),
                        eq("LIKE"),
                        eq(Map.of("id", postId, "counts", counts))
                );
    }

    @Test
    @DisplayName("통계 없데이트 - RabbitMQ 메시지 전송 확인(COMMENT)")
    void updateCommentCountsWithSendMessageToRabbitMQ() {
        Long postId = 1L;
        StatType statType = StatType.COMMENT;
        int counts = 5;

        this.postsStatisticsService.updateCounts(postId, statType, counts);

        verify(this.rabbitTemplate, times(1))
                .convertAndSend(
                        eq("post.stats.exchange"),
                        eq("COMMENT"),
                        eq(Map.of("id", postId, "counts", counts))
                );
    }

    @Test
    @DisplayName("통계 조회 - VIEW")
    void getStatsWithView() {
        Long postId = 1L;

        PostsStatistics statistics = PostsStatistics.builder()
                .id(postId)
                .viewCounts(100L)
                .likeCounts(50L)
                .commentCounts(30L)
                .build();

        given(this.postsStatisticsRepository.findById(postId)).willReturn(
                statistics
        );

        PostStatDto stats = this.postsStatisticsService.getStats(List.of(StatType.VIEW), postId);
        Assertions.assertThat(stats.getViewCounts()).isEqualTo(100L);
        Assertions.assertThat(stats.getLikeCounts()).isNull();
        Assertions.assertThat(stats.getCommentCounts()).isNull();
    }

    @Test
    @DisplayName("통계 조회 - 빈 리스트")
    void getStatsWithEmptyList() {
        Long postId = 1L;

        PostsStatistics statistics = PostsStatistics.builder()
                .id(postId)
                .viewCounts(100L)
                .likeCounts(50L)
                .commentCounts(30L)
                .build();

        given(this.postsStatisticsRepository.findById(postId)).willReturn(
                statistics
        );

        PostStatDto stats = this.postsStatisticsService.getStats(List.of(), postId);
        Assertions.assertThat(stats.getViewCounts()).isEqualTo(100L);
        Assertions.assertThat(stats.getLikeCounts()).isEqualTo(50L);
        Assertions.assertThat(stats.getCommentCounts()).isEqualTo(30L);
    }
}
