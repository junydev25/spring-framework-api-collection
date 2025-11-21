package com.junydev.spring.api.board.service;


import com.junydev.spring.api.board.internal.dto.Category;
import com.junydev.spring.api.board.internal.dto.PostDto;
import com.junydev.spring.api.board.internal.entity.Posts;
import com.junydev.spring.api.board.internal.entity.PostsDetail;
import com.junydev.spring.api.board.internal.entity.PostsStatistics;
import com.junydev.spring.api.board.repository.PostsRepository;
import com.junydev.spring.api.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostsRepository postsRepository;

    @InjectMocks
    private PostsService postsService;

    @Test
    @DisplayName("Repository로부터 Post Entity 가져와서 PostDto로 반환")
    void getPostDtoSuccess() {

        Posts entity = createPostsEntity(
                1L,
                "title",
                "author",
                "content",
                Category.GAME
        );

        given(this.postsRepository.findById(any(Long.class))).willReturn(entity);

        PostDto postDto = this.postsService.getPost(1L);
        Assertions.assertThat(postDto.getId()).isEqualTo(1L);
        Assertions.assertThat(postDto.getTitle()).isEqualTo("title");
        Assertions.assertThat(postDto.getAuthor()).isEqualTo("author");
        Assertions.assertThat(postDto.getCategory()).isEqualTo(Category.GAME);
        Assertions.assertThat(postDto.getContent()).isEqualTo("content");
    }

    @Test
    @DisplayName("저장된 모든 포스트 가져오기")
    void getAllPostsSuccessWhenCategoryAndAuthorIsNull() {
        given(this.postsRepository.findAll(any(Category.class), anyString()))
                .willReturn(
                        IntStream.range(0, 10).mapToObj(i ->
                            createPostsEntity(
                                    Long.valueOf(i),
                                    "title" + i,
                                    "author" + i,
                                    "content" + i,
                                    Category.GAME
                            )
                        ).toList()
                );

        List<PostDto> posts = this.postsService.getPosts(Category.GAME, "author");
        Assertions.assertThat(posts.size()).isEqualTo(10);
    }

    @Test
    @DisplayName("포스트 생성")
    void createPostSuccess() {
        given(this.postsRepository.save(any(Posts.class))).willReturn(
                createPostsEntity(1L, "title", "author", "content", Category.GAME)
        );

        PostDto postDto = PostDto.builder()
                .title("title")
                .author("author")
                .category(Category.GAME)
                .content("content")
                .build();

        PostDto result = this.postsService.createPost(postDto);

        Assertions.assertThat(result.getId()).isEqualTo(1L);
        Assertions.assertThat(result.getTitle()).isEqualTo("title");
        Assertions.assertThat(result.getAuthor()).isEqualTo("author");
        Assertions.assertThat(result.getCategory()).isEqualTo(Category.GAME);
        Assertions.assertThat(result.getContent()).isEqualTo("content");
    }

    @Test
    @DisplayName("포스트 업데이트")
    void updatePostSuccess() {
        Posts entity = createPostsEntity(
                1L,
                "title",
                "author",
                "content",
                Category.IT
        );
        given(this.postsRepository.findById(any(Long.class))).willReturn(entity);

        PostDto postDto = this.postsService.updatePost(PostDto.builder()
                    .id(1L)
                    .title("title2")
                    .content("content2")
                    .category(Category.GAME)
                    .build()
        );

        Assertions.assertThat(postDto.getTitle()).isEqualTo("title2");
        Assertions.assertThat(postDto.getContent()).isEqualTo("content2");
        Assertions.assertThat(postDto.getCategory()).isEqualTo(Category.GAME);
    }

    Posts createPostsEntity(Long id, String title, String author, String content, Category category) {
        Posts entity = Posts.builder()
                .id(id)
                .title(title)
                .author(author)
                .category(category)
                .build();

        PostsStatistics statistics = PostsStatistics.builder()
                .id(id)
                .posts(entity)
                .viewCounts(0L)
                .likeCounts(0L)
                .commentCounts(0L)
                .build();

        PostsDetail contents = PostsDetail.builder()
                .id(id)
                .posts(entity)
                .content(content)
                .build();

        entity.setStatistic(statistics);
        entity.setDetail(contents);

        return entity;
    }

}
