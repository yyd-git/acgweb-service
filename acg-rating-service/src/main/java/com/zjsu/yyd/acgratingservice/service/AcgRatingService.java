package com.zjsu.yyd.acgratingservice.service;

import com.zjsu.yyd.acgratingservice.feign.AcgProductFeignClient;
import com.zjsu.yyd.acgratingservice.feign.UserFeignClient;
import com.zjsu.yyd.acgratingservice.model.AcgRating;
import com.zjsu.yyd.acgratingservice.model.Result;
import com.zjsu.yyd.acgratingservice.repository.AcgRatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcgRatingService {

    private final AcgRatingRepository ratingRepository;
    private final UserFeignClient userFeignClient;
    private final AcgProductFeignClient productFeignClient;

    public AcgRatingService(
            AcgRatingRepository ratingRepository,
            UserFeignClient userFeignClient,
            AcgProductFeignClient productFeignClient) {
        this.ratingRepository = ratingRepository;
        this.userFeignClient = userFeignClient;
        this.productFeignClient = productFeignClient;
    }

    /**
     * 用户评分（一人一产品一次，可覆盖）
     */
    @Transactional
    public Result rate(Long userId, Long productId, Integer score) {

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

        // 3. 新增或更新评分（一人一产品一条）
        ratingRepository.findByUserIdAndProductIdAndIsDeletedFalse(userId, productId)
                .ifPresentOrElse(rating -> {
                    rating.setScore(score);
                    ratingRepository.save(rating);
                }, () -> {
                    AcgRating rating = new AcgRating();
                    rating.setUserId(userId);
                    rating.setProductId(productId);
                    rating.setScore(score);
                    ratingRepository.save(rating);
                });

        // 4. 重新计算平均分
        Double avgScore = ratingRepository.calculateAvgScore(productId);

        // 5. 回写产品服务
        productFeignClient.updateScore(productId, avgScore);

        return Result.success("评分成功");
    }
}
