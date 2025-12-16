package com.zjsu.yyd.acgratingservice.controller;

import com.zjsu.yyd.acgratingservice.model.Result;
import com.zjsu.yyd.acgratingservice.service.AcgRatingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/acg-rating")
public class AcgRatingController {

    private final AcgRatingService ratingService;

    public AcgRatingController(AcgRatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/rate")
    public Result rate(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer score) {
        ratingService.rate(userId, productId, score);
        return Result.success("评分成功");
    }
}
