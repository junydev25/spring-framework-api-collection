package com.junydev.spring.api.board.internal.dto;

import com.junydev.spring.api.board.internal.entity.Posts;
import com.junydev.spring.api.board.internal.entity.PostsComment;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
@Data
@ToString
public class PostDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private Category category;
    private PostStatDto statistics;
    private Map<String, Object> comments;
    private Instant createdAt;
    private Instant updatedAt;

    public static PostDto of(Posts posts) {
        if (posts == null) {
            return null;
        }

        List<PostCommentDto> postComments = posts.getComments().stream().map(PostCommentDto::of).toList();

        return PostDto.builder()
                .id(posts.getId())
                .author(posts.getAuthor())
                .title(posts.getTitle())
                .category(posts.getCategory())
                .content(posts.getDetail().getContent())
                .statistics(PostStatDto.of(posts.getStatistic()))
                .comments(
                        Map.of("totalCount", postComments.size(),
                                "data", postComments)
                )
                .createdAt(posts.getCreatedAt())
                .updatedAt(posts.getUpdatedAt())
                .build();
    }
}
