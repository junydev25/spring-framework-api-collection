package com.junydev.spring.api.board.service;

import com.junydev.spring.api.board.internal.dto.PostCommentDto;
import com.junydev.spring.api.board.internal.dto.StatType;
import com.junydev.spring.api.board.internal.entity.Posts;
import com.junydev.spring.api.board.internal.entity.PostsComment;
import com.junydev.spring.api.board.repository.PostsCommentRepository;
import com.junydev.spring.api.board.repository.PostsRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class PostsCommentService {

    private final PostsCommentRepository postsCommentRepository;
    private final PostsRepository postsRepository;
    private final RabbitTemplate rabbitTemplate;

    public PostsCommentService(PostsCommentRepository postsCommentRepository, PostsRepository postsRepository, RabbitTemplate rabbitTemplate) {
        this.postsCommentRepository = postsCommentRepository;
        this.postsRepository = postsRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public PostCommentDto writeComment(PostCommentDto postCommentDto) {
        Long postId = postCommentDto.getPostId();
        Posts posts = this.postsRepository.findById(postId);

        PostsComment comment = PostsComment.builder()
                .posts(posts)
                .author(postCommentDto.getAuthor())
                .comment(postCommentDto.getComment())
                .build();

        posts.getComments().add(comment);

        PostsComment savedPostComment = this.postsCommentRepository.save(comment);
        this.rabbitTemplate.convertAndSend(
                "post.stats.exchange",
                StatType.COMMENT.toString(),
                Map.of("id", postId, "counts", 1));

        return PostCommentDto.of(savedPostComment);
    }

    @Transactional
    public PostCommentDto modifyComment(PostCommentDto postCommentDto) {
        PostsComment postsComment = this.postsCommentRepository.findById(postCommentDto.getId());
        postsComment.setComment(postCommentDto.getComment());
        postsComment.setUpdatedAt(Instant.now());

        return PostCommentDto.of(postsComment);
    }

    @Transactional
    public void remove(Long postId, Long commentId) {
        PostsComment postsComment = this.postsCommentRepository.findById(commentId);

        Posts posts = this.postsRepository.findById(postId);
        posts.getComments().remove(postsComment);

        this.postsCommentRepository.remove(commentId);
        this.rabbitTemplate.convertAndSend(
                "post.stats.exchange",
                StatType.COMMENT.toString(),
                Map.of("id", postId, "counts", -1));
    }
}
