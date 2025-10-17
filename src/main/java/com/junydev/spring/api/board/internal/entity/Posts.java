package com.junydev.spring.api.board.internal.entity;

import com.junydev.spring.api.board.internal.dto.Category;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // 작성자(author)
    @Column(nullable = false, length = 10)
    private String author;

    // 제목(title)
    @Column(nullable = false, length = 100)
    private String title;

    // 카테고리(category)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    // 최초 작성 일자(createdAt)
    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    // 마지막 수정 일자(updatedAt)
    @LastModifiedDate
    @Column(updatable = true)
    private Instant updatedAt;

    @OneToOne(mappedBy = "posts", cascade = CascadeType.ALL)
    private PostsStatistics statistic;

    @OneToOne(mappedBy = "posts", cascade = CascadeType.ALL)
    private PostsDetail detail;

    @OneToMany(mappedBy = "posts")
    private List<PostsComment> comments;
}
