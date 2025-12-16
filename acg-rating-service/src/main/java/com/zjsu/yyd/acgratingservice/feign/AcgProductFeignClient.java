package com.zjsu.yyd.acgratingservice.feign;


import com.zjsu.yyd.acgratingservice.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "acg-product-service")
public interface AcgProductFeignClient {

    @PutMapping("/acg-product/{id}/score")
    Result updateScore(
            @PathVariable("id") Long productId,
            @RequestParam("score") Double score);

    @GetMapping("/acg-product/{id}")
    Result getById(@PathVariable("id") Long id);
}
