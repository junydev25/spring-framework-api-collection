package com.junydev.spring.api.board.repository;

import com.junydev.spring.api.board.internal.entity.PostsComment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PostsCommentRepository {

    @PersistenceContext
    private EntityManager em;

    public PostsComment findById(Long id) {
        return this.em.find(PostsComment.class, id);
    }

    public PostsComment save(PostsComment postsComment) {
        this.em.persist(postsComment);
        return postsComment;
    }

    public void remove(Long commentId) {
        this.em.remove(this.findById(commentId));
    }
}
