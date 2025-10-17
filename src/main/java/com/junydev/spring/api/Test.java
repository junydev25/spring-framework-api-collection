package com.junydev.spring.api;

import com.junydev.spring.api.board.internal.dto.Category;
import com.junydev.spring.api.board.internal.entity.Posts;
import com.junydev.spring.api.board.internal.entity.PostsComment;
import com.junydev.spring.api.board.internal.entity.PostsDetail;
import com.junydev.spring.api.board.internal.entity.PostsStatistics;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class Test implements CommandLineRunner {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Posts post = Posts.builder()
                .title("첫 번째 제목")
                .author("김준영")
                .category(Category.IT)
                .build();
        PostsStatistics statistic = PostsStatistics.builder()
                .posts(post)
                .viewCounts(1L)
                .likeCounts(2L)
                .commentCounts(1L)
                .build();
        PostsDetail detail = PostsDetail.builder()
                .posts(post)
                .content("첫 번째 내용")
                .build();

        PostsComment comment = PostsComment.builder()
                .posts(post)
                .author("KJY")
                .comment("첫 번째 댓글")
                .build();

        post.setStatistic(statistic);
        post.setDetail(detail);
        post.setComments(List.of(comment));

        em.persist(post);
        em.persist(comment);
    }
}
