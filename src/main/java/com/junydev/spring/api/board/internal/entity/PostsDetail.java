package com.junydev.spring.api.board.internal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostsDetail {

    @Id
    private Long id;

    // Posts
    @OneToOne
    @MapsId
    @JoinColumn(name = "POSTS_ID")
    private Posts posts;

    // 내용(content)
    @Lob
    @Column(nullable = false)
    private String content;
}
