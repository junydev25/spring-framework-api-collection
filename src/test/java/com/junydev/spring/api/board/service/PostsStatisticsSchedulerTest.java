package com.junydev.spring.api.board.service;

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

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostsStatisticsSchedulerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private PostsStatisticsRepository postsStatisticsRepository;

    @InjectMocks
    private PostsStatisticsScheduler postsStatisticsScheduler;

    @Test
    @DisplayName("VIEW 통계 - 메시지가 없을 때")
    void pullPostsViewStatWithNoMessage() {
        given(this.rabbitTemplate.receiveAndConvert("post.stats.view")).willReturn(null);

        this.postsStatisticsScheduler.pullPostStatViewMessages();

        verify(this.rabbitTemplate, times(1)).receiveAndConvert("post.stats.view");
        verify(this.postsStatisticsRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("VIEW 통계 - 메시지가 하나만 있을 때")
    void pullPostsViewStatWithSingleMessage() {
        Long postId = 1L;
        PostsStatistics statistics = PostsStatistics.builder()
                .id(postId)
                .viewCounts(100L)
                .build();

        given(this.rabbitTemplate.receiveAndConvert("post.stats.view"))
                .willReturn(
                        Map.of("id", postId, "counts", 5)
                )
                .willReturn(
                        null
                );
        given(this.postsStatisticsRepository.findById(postId))
                .willReturn(statistics);

        this.postsStatisticsScheduler.pullPostStatViewMessages();

        verify(this.rabbitTemplate, times(2)).receiveAndConvert("post.stats.view");
        verify(this.postsStatisticsRepository, times(1)).findById(postId);
        Assertions.assertThat(statistics.getViewCounts()).isEqualTo(105L);
    }

    @Test
    @DisplayName("VIEW 통계 - 다른 ID의 메시지 여러 개")
    void pullPostsViewStatWithMultipleMessageForDifferentIds() {
        PostsStatistics statistics1 = PostsStatistics.builder()
                .id(1L)
                .viewCounts(100L)
                .build();

        PostsStatistics statistics2 = PostsStatistics.builder()
                .id(2L)
                .viewCounts(200L)
                .build();

        given(this.rabbitTemplate.receiveAndConvert("post.stats.view"))
                .willReturn(Map.of("id", 1L, "counts", 5))
                .willReturn(Map.of("id", 2L, "counts", 15))
                .willReturn(Map.of("id", 1L, "counts", 5))
                .willReturn(null);

        given(this.postsStatisticsRepository.findById(1L)).willReturn(statistics1);
        given(this.postsStatisticsRepository.findById(2L)).willReturn(statistics2);

        this.postsStatisticsScheduler.pullPostStatViewMessages();

        verify(this.postsStatisticsRepository, times(1)).findById(1L);
        verify(this.postsStatisticsRepository, times(1)).findById(2L);
        Assertions.assertThat(statistics1.getViewCounts()).isEqualTo(110L);
        Assertions.assertThat(statistics2.getViewCounts()).isEqualTo(215L);
    }

    @Test
    @DisplayName("VIEW 통계 - BATCH_SIZE 제한 확인")
    void pullPostsViewStatWithBatchSize() {
        given(this.rabbitTemplate.receiveAndConvert("post.stats.view"))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1))
                .willReturn(Map.of("id", 1L, "counts", 1));

        PostsStatistics statistics = PostsStatistics.builder()
                .id(1L)
                .viewCounts(0L)
                .build();

        given(this.postsStatisticsRepository.findById(1L)).willReturn(statistics);

        this.postsStatisticsScheduler.pullPostStatViewMessages();

        verify(this.rabbitTemplate, times(10)).receiveAndConvert("post.stats.view");
        Assertions.assertThat(statistics.getViewCounts()).isEqualTo(10L);
    }
}
