package com.zjsu.yyd.acgratingservice.feign;


import com.zjsu.yyd.acgratingservice.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @GetMapping("/user/{id}")
    Result getUserById(@PathVariable("id") Long id);
}
