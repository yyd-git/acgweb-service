package com.zjsu.yyd.acgratingservice.controller;

import com.zjsu.yyd.acgratingservice.model.AcgComment;
import com.zjsu.yyd.acgratingservice.model.Result;
import com.zjsu.yyd.acgratingservice.service.AcgCommentService;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/acg-comment")
public class AcgCommentController {

    private final AcgCommentService commentService;

    public AcgCommentController(AcgCommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/list/{productId}")
    public Result listByProduct(@PathVariable Long productId) {
        return commentService.listByProduct(productId);
    }

    @PostMapping("/add")
    public Result add(@RequestParam Long userId,
                      @RequestParam Long productId,
                      @RequestParam String content) {
        return commentService.add(userId, productId, content);
    }
}

