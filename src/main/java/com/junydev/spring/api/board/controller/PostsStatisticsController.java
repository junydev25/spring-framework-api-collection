package com.junydev.spring.api.board.controller;

import com.junydev.spring.api.board.internal.dto.StatType;
import com.junydev.spring.api.board.service.PostsStatisticsService;
import com.junydev.spring.api.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostsStatisticsController {

    private final PostsStatisticsService postsStatisticsService;

    public PostsStatisticsController(PostsStatisticsService postsStatisticsService) {
        this.postsStatisticsService = postsStatisticsService;
    }

    @GetMapping("/api/posts/{id}/stat")
    public ApiResponse getStat(@RequestParam(required = true) List<StatType> stats,
                               @PathVariable Long id) {
        return ApiResponse.builder()
                .status("success")
                .data(this.postsStatisticsService.getStats(stats, id))
                .build();
    }

    @PostMapping("/api/posts/{id}/stat/{type}")
    public ApiResponse updateCounts(@PathVariable Long id,
                                    @PathVariable StatType type,
                                    @RequestParam int counts) {
        this.postsStatisticsService.updateCounts(id, type, counts);
        return ApiResponse.builder()
                .status("success")
                .build();
    }
}
