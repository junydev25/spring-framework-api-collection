package com.junydev.spring.api.board.controller;

import com.junydev.spring.api.board.internal.dto.PostCommentDto;
import com.junydev.spring.api.board.service.PostsCommentService;
import com.junydev.spring.api.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostsCommentController {

    private final PostsCommentService postsCommentService;

    public PostsCommentController(PostsCommentService postsCommentService) {
        this.postsCommentService = postsCommentService;
    }

    @PostMapping("/api/posts/{postId}/comment")
    public ApiResponse writeComment(@PathVariable Long postId,
                                    @RequestBody PostCommentDto postCommentDto) {
        if (postCommentDto.getPostId() == null) {
            postCommentDto.setPostId(postId);
        }
        return ApiResponse.builder()
                .status("success")
                .data(this.postsCommentService.writeComment(postCommentDto))
                .build();
    }

    @PatchMapping("/api/posts/{postId}/comment")
    public ApiResponse modifyComment(@PathVariable Long postId,
                                     @RequestBody PostCommentDto postCommentDto) {
        if (postCommentDto.getPostId() == null) {
            postCommentDto.setPostId(postId);
        }
        return ApiResponse.builder()
                .status("success")
                .data(this.postsCommentService.modifyComment(postCommentDto))
                .build();
    }

    @DeleteMapping("/api/posts/{postId}/comment/{commentId}")
    public ApiResponse deleteComment(@PathVariable Long postId,
                                     @PathVariable Long commentId) {
        this.postsCommentService.remove(postId, commentId);
        return ApiResponse.builder()
                .status("success")
                .message("Comment with id " + commentId + " of Post with id " + postId + " has been deleted")
                .build();
    }
}
