package com.junydev.spring.api.board.repository;

import com.junydev.spring.api.board.internal.dto.Category;
import com.junydev.spring.api.board.internal.entity.Posts;
import com.junydev.spring.api.board.internal.entity.PostsDetail;
import com.junydev.spring.api.board.internal.entity.PostsStatistics;
import com.junydev.spring.api.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Math.floor;

@DataJpaTest
@Import(PostsRepository.class)
public class PostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostsRepository postsRepository;

    @Test
    @DisplayName("포스트 조회 - DB에 있는 경우")
    void findExistData() {
        Posts posts = Posts.builder()
                .title("title")
                .author("author")
                .category(Category.GAME)
                .build();

        PostsStatistics statistics = PostsStatistics.builder()
                .posts(posts)
                .viewCounts(0L)
                .likeCounts(0L)
                .commentCounts(0L)
                .build();

        PostsDetail detail = PostsDetail.builder()
                .posts(posts)
                .content("content")
                .build();

        posts.setStatistic(statistics);
        posts.setDetail(detail);

        this.entityManager.persist(posts);

        Posts postsFromDB = this.postsRepository.findById(1L);
        Assertions.assertThat(postsFromDB).isNotNull();
        Assertions.assertThat(postsFromDB.getTitle()).isEqualTo("title");
        Assertions.assertThat(postsFromDB.getAuthor()).isEqualTo("author");
        Assertions.assertThat(postsFromDB.getCategory()).isEqualTo(Category.GAME);
        Assertions.assertThat(postsFromDB.getDetail().getContent()).isEqualTo("content");
    }

    @Test
    @DisplayName("포스트 조회 - DB에 없는 경우")
    void findNotExistData() {
        Assertions.assertThatThrownBy(() -> this.postsRepository.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("조건없이 모든 포스트 가져오기")
    void findAll() {
        IntStream.range(1, 11).mapToObj(i -> createPostsEntity(
                "title" + i,
                "author" + i,
                "content" + i,
                Category.values()[i % Category.values().length]
        )).forEach(this.entityManager::persist);

        List<Posts> posts = this.postsRepository.findAll(null, null);
        Assertions.assertThat(posts).hasSize(10);
    }

    @Test
    @DisplayName("카테고리 조건에 맞는 모든 포스트 가져오기")
    void findAllFilteringCategory() {
        int startIdx = 1;
        int endIdx = 11;
        Category filteredCategory = Category.values()[0];
        int length = Category.values().length;
        int categoryCount = (int) floor((endIdx-1) / length) -
                (int) floor((startIdx-1) / length);
        IntStream.range(1, 11).mapToObj(i -> createPostsEntity(
                "title" + i,
                "author" + i,
                "content" + i,
                Category.values()[i % Category.values().length]
        )).forEach(this.entityManager::persist);

        List<Posts> posts = this.postsRepository.findAll(filteredCategory, null);
        Assertions.assertThat(posts).hasSize(categoryCount);
    }

    @Test
    @DisplayName("작성자 조건에 맞는 모든 포스트 가져오기")
    void findAllFilteringAuthor() {
        IntStream.range(1, 11).mapToObj(i -> createPostsEntity(
                "title" + i,
                "author" + i,
                "content" + i,
                Category.values()[i % Category.values().length]
        )).forEach(this.entityManager::persist);

        List<Posts> posts = this.postsRepository.findAll(null, "author1");
        Assertions.assertThat(posts).hasSize(1);
    }

    Posts createPostsEntity(String title, String author, String content, Category category) {
        Posts entity = Posts.builder()
                .title(title)
                .author(author)
                .category(category)
                .build();

        PostsStatistics statistics = PostsStatistics.builder()
                .posts(entity)
                .viewCounts(0L)
                .likeCounts(0L)
                .commentCounts(0L)
                .build();

        PostsDetail contents = PostsDetail.builder()
                .posts(entity)
                .content(content)
                .build();

        entity.setStatistic(statistics);
        entity.setDetail(contents);

        return entity;
    }
}
