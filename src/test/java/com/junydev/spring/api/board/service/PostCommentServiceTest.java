package com.junydev.spring.api.board.service;

import com.junydev.spring.api.board.internal.dto.PostCommentDto;
import com.junydev.spring.api.board.internal.dto.StatType;
import com.junydev.spring.api.board.internal.entity.Posts;
import com.junydev.spring.api.board.internal.entity.PostsComment;
import com.junydev.spring.api.board.repository.PostsCommentRepository;
import com.junydev.spring.api.board.repository.PostsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
public class PostCommentServiceTest {

    @Mock
    private PostsCommentRepository postsCommentRepository;

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PostsCommentService postsCommentService;

    @Test
    @DisplayName("댓글 작성")
    void writeComment() {
        Long postId = 1L;

        PostCommentDto postCommentDto = PostCommentDto.builder()
                .id(1L)
                .postId(postId)
                .author("author")
                .comment("comment")
                .build();

        Posts posts = Posts.builder()
                .id(postId)
                .build();

        given(this.postsRepository.findById(postId)).willReturn(posts);
        given(this.postsCommentRepository.save(any(PostsComment.class))).willReturn(
                PostsComment.builder()
                        .posts(posts)
                        .author(postCommentDto.getAuthor())
                        .comment(postCommentDto.getComment())
                        .build()
        );
        willDoNothing().given(this.rabbitTemplate).convertAndSend(
                "post.stats.exchange",
                StatType.COMMENT.toString(),
                Map.of("id", postId, "counts", 1)
        );

        PostCommentDto result = this.postsCommentService.writeComment(postCommentDto);

        Assertions.assertThat(result.getComment()).isEqualTo(postCommentDto.getComment());
        Assertions.assertThat(posts.getComments()).hasSize(1);
    }

    @Test
    @DisplayName("댓글 수정")
    void modifyComment() {
        Long postId = 1L;

        PostCommentDto postCommentDto = PostCommentDto.builder()
                .id(1L)
                .postId(postId)
                .author("author")
                .comment("new comment")
                .build();

        Posts posts = Posts.builder()
                .id(postId)
                .build();

        given(this.postsCommentRepository.findById(postCommentDto.getId())).willReturn(
                PostsComment.builder()
                        .id(1L)
                        .posts(posts)
                        .comment("Comment")
                        .updatedAt(Instant.now().minus(3, ChronoUnit.MINUTES))
                        .build()
        );

        PostCommentDto result = this.postsCommentService.modifyComment(postCommentDto);

        Assertions.assertThat(result.getComment()).isEqualTo(postCommentDto.getComment());
        Assertions.assertThat(result.getUpdatedAt()).isCloseTo(Instant.now(), Assertions.within(1, ChronoUnit.MINUTES));
    }

    @Test
    @DisplayName("댓글 제거")
    void deleteComment() {
        Long postId = 1L;
        Long commentId = 1L;

        Posts posts = Posts.builder()
                .id(postId)
                .build();

        PostsComment postsComment = PostsComment.builder()
                .id(commentId)
                .author("author")
                .comment("new comment")
                .posts(posts)
                .build();

        posts.getComments().add(postsComment);

        given(this.postsCommentRepository.findById(commentId)).willReturn(postsComment);
        given(this.postsRepository.findById(postId)).willReturn(posts);
        willDoNothing().given(this.postsCommentRepository).remove(postsComment);
        willDoNothing().given(this.rabbitTemplate).convertAndSend(
                "post.stats.exchange",
                StatType.COMMENT.toString(),
                Map.of("id", postId, "counts", -1)
        );

        this.postsCommentService.remove(postId, commentId);

        Assertions.assertThat(posts.getComments()).hasSize(0);
    }
}
