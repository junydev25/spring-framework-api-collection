package com.junydev.spring.api.board.internal.dto;

import com.junydev.spring.api.board.internal.entity.PostsComment;
import lombok.*;

import java.time.Instant;

@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentDto {

    private Long id;
    private Long postId;
    private String author;
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;

    public static PostCommentDto of(PostsComment postsComment) {
        if (postsComment == null) {
            return null;
        }

        return PostCommentDto.builder()
                .id(postsComment.getId())
                .postId(postsComment.getPosts().getId())
                .author(postsComment.getAuthor())
                .comment(postsComment.getComment())
                .createdAt(postsComment.getCreatedAt())
                .updatedAt(postsComment.getUpdatedAt())
                .build();
    }
}
