package com.junydev.spring.api.board.service;

import com.junydev.spring.api.board.internal.entity.PostsStatistics;
import com.junydev.spring.api.board.repository.PostsStatisticsRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
public class PostsStatisticsScheduler {

    private final static Integer BATCH_SIZE = 10;

    private final RabbitTemplate rabbitTemplate;
    private final PostsStatisticsRepository postsStatisticsRepository;

    public PostsStatisticsScheduler(RabbitTemplate rabbitTemplate, PostsStatisticsRepository postsStatisticsRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.postsStatisticsRepository = postsStatisticsRepository;
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void pullPostStatViewMessages() {
        Map<Long, Integer> viewCountsMap = summaryStat("post.stats.view");
        viewCountsMap.forEach((id, counts) -> {
            PostsStatistics statistics = this.postsStatisticsRepository.findById(id);
            statistics.setViewCounts(statistics.getViewCounts() + counts);
        });
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void pullPostStatLikeMessages() {
        Map<Long, Integer> likeCountsMap = summaryStat("post.stats.like");
        likeCountsMap.forEach((id, counts) -> {
            PostsStatistics statistics = this.postsStatisticsRepository.findById(id);
            statistics.setLikeCounts(statistics.getLikeCounts() + counts);
        });
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void pullPostStatCommentMessages() {
        Map<Long, Integer> commentCountsMap = summaryStat("post.stats.comment");
        commentCountsMap.forEach((id, counts) -> {
            PostsStatistics statistics = this.postsStatisticsRepository.findById(id);
            statistics.setCommentCounts(statistics.getCommentCounts() + counts);
        });
    }

    private Map<Long, Integer> summaryStat(String queueName) {
        HashMap<Long, Integer> summaryMap = new HashMap<>();
        int iter = 0;
        while (true) {
            Map<String, Object> data = (Map<String, Object>) this.rabbitTemplate.receiveAndConvert(queueName);

            if (data == null) {
                break;
            }

            Long id = ((Number) data.get("id")).longValue();
            int counts = ((Number) data.get("counts")).intValue();
            if (summaryMap.containsKey(id)) {
                summaryMap.put(id, summaryMap.get(id) + counts);
            } else {
                summaryMap.put(id, counts);
            }

            iter += 1;

            if (iter >= BATCH_SIZE) {
                break;
            }
        }
        return summaryMap;
    }
}
