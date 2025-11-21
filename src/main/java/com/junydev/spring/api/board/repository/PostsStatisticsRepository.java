package com.junydev.spring.api.board.repository;

import com.junydev.spring.api.board.internal.entity.PostsStatistics;
import com.junydev.spring.api.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PostsStatisticsRepository {

    @PersistenceContext
    private EntityManager em;

    public PostsStatistics findById(Long id) {
        PostsStatistics postsStatistics = this.em.find(PostsStatistics.class, id);
        if (postsStatistics == null) {
            throw new ResourceNotFoundException("게시물의 통계 자료를 찾을 수 없습니다.(id=%d)".formatted(id));
        }
        return postsStatistics;
    }
}
