package com.junydev.spring.api.board.controller;

import com.junydev.spring.api.board.internal.dto.Category;
import com.junydev.spring.api.board.internal.dto.PostDto;
import com.junydev.spring.api.board.service.PostsService;
import com.junydev.spring.api.common.ApiResponse;
import com.junydev.spring.api.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PostsController {

    private final PostsService postsService;

    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    @GetMapping("/api/posts/{id}")
    public ApiResponse getPost(@PathVariable Long id) {
        PostDto post = this.postsService.getPost(id);
        if (post == null) {
            throw new ResourceNotFoundException("게시물을 찾을 수 없습니다.(id=%d)".formatted(id));
        }
        return ApiResponse.builder()
                .status("success")
                .data(post)
                .build();
    }

    @GetMapping("/api/posts")
    public ApiResponse getPosts(@RequestParam(required = false) Category category,
                                @RequestParam(required = false) String author) {
        List<PostDto> posts = this.postsService.getPosts(category, author);
        return ApiResponse.builder()
                .status("success")
                .data(Map.of("totalCount", posts.size(),
                        "posts", posts))
                .build();
    }

    @PostMapping("/api/posts")
    public ApiResponse createPost(@RequestBody PostDto postDto) {
        return ApiResponse.builder()
                .status("success")
                .data(this.postsService.createPost(postDto))
                .build();
    }

    @PutMapping("/api/posts/{id}")
    public ApiResponse updatePost(@PathVariable Long id,
                                  @RequestBody PostDto postDto) {
        return ApiResponse.builder()
                .status("success")
                .data(this.postsService.updatePost(postDto))
                .build();
    }

    @DeleteMapping("/api/posts/{id}")
    public ApiResponse deletePost(@PathVariable Long id) {
        this.postsService.deletePost(id);
        return ApiResponse.builder()
                .status("success")
                .message("Post with id " + id + " has been deleted")
                .build();
    }
}
