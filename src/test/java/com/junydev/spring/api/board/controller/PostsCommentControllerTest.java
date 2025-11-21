package com.junydev.spring.api.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junydev.spring.api.board.internal.dto.PostCommentDto;
import com.junydev.spring.api.board.service.PostsCommentService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@WebMvcTest(value = PostsCommentController.class)
public class PostsCommentControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostsCommentService postsCommentService;

    @Test
    @DisplayName("댓글 작성")
    void writeCommentSuccess() throws JsonProcessingException {
        Long postId = 1L;

        given(this.postsCommentService.writeComment(any(PostCommentDto.class))).willReturn(
                PostCommentDto.builder()
                        .id(1L)
                        .postId(postId)
                        .comment("comment")
                        .author("author")
                        .build()
        );


        MvcTestResult result = this.mvc.post().uri("/api/posts/{postId}/comment", postId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(
                        PostCommentDto.builder()
                                .postId(postId)
                                .comment("comment")
                                .author("author")
                                .build()
                ))
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.status").isEqualTo("success");
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data.id").isEqualTo(1);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.message").isNull();
    }

    @Test
    @DisplayName("댓글 수정")
    void modifyCommentSuccess() throws JsonProcessingException {
        Long postId = 1L;

        given(this.postsCommentService.modifyComment(any(PostCommentDto.class))).willReturn(
                PostCommentDto.builder()
                        .id(1L)
                        .postId(postId)
                        .comment("comment")
                        .author("author")
                        .build()
        );


        MvcTestResult result = this.mvc.patch().uri("/api/posts/{postId}/comment", postId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(
                        PostCommentDto.builder()
                                .postId(postId)
                                .comment("comment")
                                .author("author")
                                .build()
                ))
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.status").isEqualTo("success");
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data.id").isEqualTo(1);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.message").isNull();
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteCommentSuccess() throws JsonProcessingException {
        Long postId = 1L;
        Long commentId = 1L;

        willDoNothing().given(this.postsCommentService).remove(eq(postId), eq(commentId));

        MvcTestResult result = this.mvc.delete().uri("/api/posts/{postId}/comment/{commentId}", postId, commentId)
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.status").isEqualTo("success");
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.message").isEqualTo(
                        "Comment with id " + commentId + " of Post with id " + postId + " has been deleted"
                );
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data").isNull();
    }
}
