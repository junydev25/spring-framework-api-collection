package com.junydev.spring.api.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junydev.spring.api.board.internal.dto.Category;
import com.junydev.spring.api.board.internal.dto.PostDto;
import com.junydev.spring.api.board.internal.dto.PostStatDto;
import com.junydev.spring.api.board.service.PostsService;
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

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@WebMvcTest(value = PostsController.class)
public class PostsControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostsService postsService;

    @Test
    @DisplayName("특정 포스트 조회 성공")
    void shouldRetrievePostSuccessfully() {
        Long postId = 1L;
        PostDto postDto = PostDto.builder()
                .id(postId)
                .title("title")
                .content("content")
                .author("author")
                .category(Category.IT)
                .build();

        given(this.postsService.getPost(postId)).willReturn(
                postDto
        );
        MvcTestResult result = this.mvc.get().uri("/api/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result)
                .bodyJson()
                .extractingPath("$.status")
                .isEqualTo("success");
        Assertions.assertThat(result)
                .bodyJson()
                .extractingPath("$.data.id")
                .isEqualTo(1);
        Assertions.assertThat(result)
                .bodyJson()
                .extractingPath("$.message")
                .isEmpty();
    }

    @Test
    @DisplayName("전체 포스트 조회 성공")
    void shouldRetrieveAllPostSuccessfully() {
        List<PostDto> posts = List.of(
                PostDto.builder()
                        .id(1L)
                        .title("title1")
                        .content("content1")
                        .author("author")
                        .category(Category.IT)
                        .build(),
                PostDto.builder()
                        .id(2L)
                        .title("title2")
                        .content("content2")
                        .author("author")
                        .category(Category.IT)
                        .build()
        );

        given(this.postsService.getPosts(Category.IT, "author")).willReturn(
                posts
        );

        MvcTestResult result = this.mvc.get().uri("/api/posts")
                .queryParam("category", "IT")
                .queryParam("author", "author")
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.status").isEqualTo("success");
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data.posts[0].id").isEqualTo(1);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data.posts[1].id").isEqualTo(2);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data.totalCount").isEqualTo(2);
    }

    @Test
    @DisplayName("빈 포스트 조회 성공")
    void shouldRetrieveEmptyPostSuccessfully() throws Exception {
        given(this.postsService.getPosts(Category.IT, "author")).willReturn(
                List.of()
        );

        MvcTestResult result = this.mvc.get().uri("/api/posts")
                .queryParam("category", "IT")
                .queryParam("author", "author")
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.status").isEqualTo("success");
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data.posts").isEmpty();
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data.totalCount").isEqualTo(0);
    }

    @Test
    @DisplayName("Post 등록 성공")
    void shouldCreatePostSuccessfully() {
        Long postId = 1L;

        given(this.postsService.createPost(any(PostDto.class))).willReturn(
                PostDto.builder()
                        .id(postId)
                        .title("title")
                        .content("content")
                        .author("author")
                        .category(Category.GAME)
                        .content("content")
                        .statistics(PostStatDto.builder()
                                .likeCounts(0L)
                                .viewCounts(0L)
                                .commentCounts(0L)
                                .build())
                        .comments(
                                Map.of("totalCount", 0,
                                "data", List.of())
                        )
                        .build()
        );

        MvcTestResult result = this.mvc.post().uri("/api/posts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "title",
                            "author": "author",
                            "content": "content",
                            "category": "GAME"
                        }
                        """)
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.status").isEqualTo("success");
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data.id").isEqualTo(1);
    }

    @Test
    @DisplayName("포스트 업데이트 성공")
    void shouldUpdatePostSuccessfully() throws JsonProcessingException {
        Long postId = 1L;
        PostDto postDto = PostDto.builder()
                .id(postId)
                .title("title")
                .content("content")
                .author("author")
                .category(Category.GAME)
                .content("content")
                .statistics(PostStatDto.builder()
                        .likeCounts(0L)
                        .viewCounts(0L)
                        .commentCounts(0L)
                        .build())
                .comments(
                        Map.of("totalCount", 0,
                                "data", List.of())
                )
                .build();

        given(this.postsService.updatePost(any(PostDto.class))).willReturn(postDto);

        MvcTestResult result = this.mvc.put().uri("/api/posts/{id}", postId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(postDto))
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.status").isEqualTo("success");
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data.id").isEqualTo(1);
    }

    @Test
    @DisplayName("포스트 삭제")
    void shouldDeletePostSuccessfully() {
        willDoNothing().given(this.postsService).deletePost(any(Long.class));

        Long postId = 1L;
        MvcTestResult result = this.mvc.delete()
                .uri("/api/posts/{id}", postId)
                .exchange();

        Assertions.assertThat(result).hasStatus(HttpStatus.OK);
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.status").isEqualTo("success");
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.message").isEqualTo(
                        "Post with id " + 1 + " has been deleted"
                );
        Assertions.assertThat(result).bodyJson()
                .extractingPath("$.data").isEmpty();
    }
}
