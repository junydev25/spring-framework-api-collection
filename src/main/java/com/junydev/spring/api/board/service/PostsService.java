package com.junydev.spring.api.board.service;

import com.junydev.spring.api.board.internal.dto.Category;
import com.junydev.spring.api.board.internal.dto.PostDto;
import com.junydev.spring.api.board.internal.entity.Posts;
import com.junydev.spring.api.board.internal.entity.PostsDetail;
import com.junydev.spring.api.board.internal.entity.PostsStatistics;
import com.junydev.spring.api.board.repository.PostsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostsService {

    private final PostsRepository postsRepository;

    public PostsService(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    public PostDto getPost(Long id) {
        Posts post = this.postsRepository.findById(id);
        return PostDto.of(post);
    }

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
        post.setComments(new ArrayList<>());

        // id 얻기 위해서 저장 후 return 해야 함
        return PostDto.of(this.postsRepository.save(post));
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        Posts post = this.postsRepository.findById(postDto.getId());
        post.setTitle(postDto.getTitle());
        post.setCategory(postDto.getCategory());

        PostsDetail detail = post.getDetail();
        detail.setContent(postDto.getContent());

        post.setUpdatedAt(Instant.now());

        return PostDto.of(post);
    }

    @Transactional
    public void deletePost(Long id) {
        this.postsRepository.remove(id);
    }
}
