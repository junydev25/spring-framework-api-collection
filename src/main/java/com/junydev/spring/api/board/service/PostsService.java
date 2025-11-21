package com.junydev.spring.api.board.service;

import com.junydev.spring.api.board.internal.dto.Category;
import com.junydev.spring.api.board.internal.dto.PostDto;
import com.junydev.spring.api.board.internal.entity.Posts;
import com.junydev.spring.api.board.internal.entity.PostsDetail;
import com.junydev.spring.api.board.internal.entity.PostsStatistics;
import com.junydev.spring.api.board.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostsService {

    private final PostsRepository postsRepository;

    @Transactional(readOnly = true)
    public PostDto getPost(Long id) {
        return PostDto.of(this.postsRepository.findById(id));
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPosts(Category category, String author) {
        return this.postsRepository.findAll(category, author).stream()
                .map(PostDto::of).toList();
    }

    @Transactional
    public PostDto createPost(PostDto postDto) {
        Posts post = Posts.builder()
                .author(postDto.getAuthor())
                .title(postDto.getTitle())
                .category(postDto.getCategory())
                .build();

        PostsStatistics statistic = PostsStatistics.builder()
                .posts(post)
                .viewCounts(0L)
                .likeCounts(0L)
                .commentCounts(0L)
                .build();

        PostsDetail detail = PostsDetail.builder()
                .posts(post)
                .content(postDto.getContent())
                .build();

        post.setStatistic(statistic);
        post.setDetail(detail);

        // id 얻기 위해서 저장 후 return 해야 함
        return PostDto.of(this.postsRepository.save(post));
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        Long id = postDto.getId();
        Posts post = this.postsRepository.findById(id);

        post.setTitle(postDto.getTitle());
        post.setCategory(postDto.getCategory());

        PostsDetail detail = post.getDetail();
        detail.setContent(postDto.getContent());

        post.setUpdatedAt(Instant.now());

        return PostDto.of(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Posts post = this.postsRepository.findById(id);
        this.postsRepository.remove(post);
    }
}
