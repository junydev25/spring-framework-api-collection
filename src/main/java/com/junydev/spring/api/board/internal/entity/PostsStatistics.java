package com.junydev.spring.api.board.internal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostsStatistics {

    @Id
    private Long id;

    // Posts
    @OneToOne
    @MapsId
    @JoinColumn(name = "POSTS_ID")
    private Posts posts;

    // 조회수(viewCount)
    @Column(name = "VIEW_COUNTS", nullable = false)
    private Long viewCounts;

    // 좋아요 수(likeCount)
    @Column(name = "LIKE_COUNTS", nullable = false)
    private Long likeCounts;

    // 댓글 수(commentCount)
    @Column(name = "COMMENT_COUNTS", nullable = false)
    private Long commentCounts;
}
