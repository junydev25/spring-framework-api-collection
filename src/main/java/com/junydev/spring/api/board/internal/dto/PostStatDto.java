package com.junydev.spring.api.board.internal.dto;

import com.junydev.spring.api.board.internal.entity.PostsStatistics;
import lombok.*;

@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostStatDto {
    private Long id;
    private Long viewCounts;
    private Long commentCounts;
    private Long likeCounts;

    public static PostStatDto of(PostsStatistics statistics) {
        if (statistics == null) {
            return null;
        }
        return PostStatDto.builder()
                .id(statistics.getId())
                .likeCounts(statistics.getLikeCounts())
                .viewCounts(statistics.getViewCounts())
                .commentCounts(statistics.getCommentCounts())
                .build();
    }
}
