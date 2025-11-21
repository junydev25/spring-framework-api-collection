package com.junydev.spring.api.board.repository;

import com.junydev.spring.api.board.internal.dto.Category;
import com.junydev.spring.api.board.internal.entity.Posts;
import com.junydev.spring.api.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostsRepository {

    @PersistenceContext
    private EntityManager em;

    public Posts findById(Long id) {
        Posts post = this.em.find(Posts.class, id);
        if (post == null) {
            throw new ResourceNotFoundException("게시물을 찾을 수 없습니다.(id=%d)".formatted(id));
        }
        return post;
    }

    public List<Posts> findAll(Category category, String author) {
        String jpql = "select p from Posts p where 1=1";
        if (category != null) {
            jpql += " and p.category = :category";
        }
        if (author != null) {
            jpql += " and p.author = :author";
        }

        TypedQuery<Posts> query = this.em.createQuery(jpql, Posts.class);

        if (category != null) {
            query.setParameter("category", category);
        }
        if (author != null) {
            query.setParameter("author", author);
        }

        return query.getResultList();
    }

    public Posts save(Posts post) {
        this.em.persist(post);
        return post;
    }

    public void remove(Posts post) {
        this.em.remove(post);
    }
}
