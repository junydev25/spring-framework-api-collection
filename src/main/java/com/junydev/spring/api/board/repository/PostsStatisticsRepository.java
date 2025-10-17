package com.junydev.spring.api.board.repository;

import com.junydev.spring.api.board.internal.entity.PostsStatistics;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PostsStatisticsRepository {

    @PersistenceContext
    private EntityManager em;

    public PostsStatistics findById(Long id) {
        return this.em.find(PostsStatistics.class, id);
    }
}
