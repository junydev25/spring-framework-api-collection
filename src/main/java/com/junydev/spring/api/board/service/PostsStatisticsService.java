package com.junydev.spring.api.board.service;

import com.junydev.spring.api.board.internal.dto.PostStatDto;
import com.junydev.spring.api.board.internal.dto.StatType;
import com.junydev.spring.api.board.internal.entity.PostsStatistics;
import com.junydev.spring.api.board.repository.PostsStatisticsRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PostsStatisticsService {

    private final PostsStatisticsRepository postsStatisticsRepository;
    private final RabbitTemplate rabbitTemplate;

    public PostsStatisticsService(PostsStatisticsRepository postsStatisticsRepository, RabbitTemplate rabbitTemplate) {
        this.postsStatisticsRepository = postsStatisticsRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void updateCounts(Long id, StatType type, int counts) {
        this.rabbitTemplate.convertAndSend("post.stats.exchange", type.toString(), Map.of("id", id, "counts", counts));
    }

    public PostStatDto getStats(List<StatType> stats, Long id) {
        PostsStatistics statistics = this.postsStatisticsRepository.findById(id);

        PostStatDto postStatDto = PostStatDto.builder().id(id).build();
        if (stats.contains(StatType.LIKE)) {
            postStatDto.setLikeCounts(statistics.getLikeCounts());
        }
        if (stats.contains(StatType.VIEW)) {
            postStatDto.setViewCounts(statistics.getViewCounts());
        }
        if (stats.contains(StatType.COMMENT)) {
            postStatDto.setCommentCounts(statistics.getCommentCounts());
        }

        return postStatDto;
    }
}
