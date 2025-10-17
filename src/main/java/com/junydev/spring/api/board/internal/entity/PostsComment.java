package com.junydev.spring.api.board.internal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class PostsComment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "POSTS_ID")
    private Posts posts;

    @Column(nullable = false, length = 10)
    private String author;

    @Lob
    @Column(nullable = false)
    private String comment;

    // 최초 작성 일자(createdAt)
    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    // 마지막 수정 일자(updatedAt)
    @LastModifiedDate
    @Column(updatable = true)
    private Instant updatedAt;
}
