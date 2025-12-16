package com.zjsu.yyd.acgratingservice.service;

import com.zjsu.yyd.acgratingservice.feign.AcgProductFeignClient;
import com.zjsu.yyd.acgratingservice.feign.UserFeignClient;
import com.zjsu.yyd.acgratingservice.model.AcgComment;
import com.zjsu.yyd.acgratingservice.model.Result;
import com.zjsu.yyd.acgratingservice.repository.AcgCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcgCommentService {

    private final AcgCommentRepository repository;
    private final UserFeignClient userFeignClient;
    private final AcgProductFeignClient productFeignClient;

    public AcgCommentService(
            AcgCommentRepository repository,
            UserFeignClient userFeignClient,
            AcgProductFeignClient productFeignClient) {
        this.repository = repository;
        this.userFeignClient = userFeignClient;
        this.productFeignClient = productFeignClient;
    }

    /**
     * 新增评论
     */
    public Result add(Long userId, Long productId, String content) {

        // 1. 校验用户是否存在
        var userResult = userFeignClient.getUserById(userId);
        if (userResult == null || userResult.getCode() != 1) {
            return Result.error("用户不存在");
        }

        // 2. 校验产品是否存在
        var productResult = productFeignClient.getById(productId);
        if (productResult == null || productResult.getCode() != 1) {
            return Result.error("产品不存在");
        }

        // 3. 保存评论
        AcgComment comment = new AcgComment();
        comment.setUserId(userId);
        comment.setProductId(productId);
        comment.setContent(content);

        AcgComment saved = repository.save(comment);
        return Result.success(saved);
    }

    /**
     * 根据产品查询评论
     */
    public Result listByProduct(Long productId) {
        List<AcgComment> comments = repository.findByProductIdAndIsDeletedFalse(productId);
        return Result.success(comments);
    }
}
