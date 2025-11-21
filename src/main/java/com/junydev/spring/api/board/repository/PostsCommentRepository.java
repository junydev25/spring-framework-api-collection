package com.junydev.spring.api.board.repository;

import com.junydev.spring.api.board.internal.entity.PostsComment;
import com.junydev.spring.api.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PostsCommentRepository {

    @PersistenceContext
    private EntityManager em;

    public PostsComment findById(Long id) {
        PostsComment postsComment = this.em.find(PostsComment.class, id);
        if (postsComment == null) {
            throw new ResourceNotFoundException("게시물을 찾을 수 없습니다.(id=%d)".formatted(id));
        }
        return postsComment;
    }

    public PostsComment save(PostsComment postsComment) {
        this.em.persist(postsComment);
        return postsComment;
    }

    public void remove(PostsComment postsComment) {
        this.em.remove(postsComment);
    }
}
