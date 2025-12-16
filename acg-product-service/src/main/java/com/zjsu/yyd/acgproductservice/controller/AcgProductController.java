package com.zjsu.yyd.acgproductservice.controller;

import com.zjsu.yyd.acgproductservice.model.AcgProduct;
import com.zjsu.yyd.acgproductservice.model.AcgProductType;
import com.zjsu.yyd.acgproductservice.model.Result;
import com.zjsu.yyd.acgproductservice.service.AcgProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/acg-product")
@Tag(name = "ACG 产品管理接口", description = "用于管理动漫 / 漫画 / 轻小说 / 游戏等 ACG 产品")
public class AcgProductController {

    private final AcgProductService service;

    public AcgProductController(AcgProductService service) {
        this.service = service;
    }

    @Operation(
            summary = "新增 ACG 产品",
            description = "新增一个 ACG 产品（动漫 / 漫画 / 轻小说 / 游戏），默认未删除"
    )
    @PostMapping
    public Result add(
            @Parameter(description = "ACG 产品对象", required = true)
            @RequestBody AcgProduct product) {
        return Result.success(service.add(product));
    }

    @Operation(
            summary = "查询所有 ACG 产品",
            description = "查询所有未被软删除的 ACG 产品"
    )
    @GetMapping
    public Result listAll() {
        return Result.success(service.listAll());
    }

    @Operation(
            summary = "按类型查询 ACG 产品",
            description = "根据产品类型查询（ANIME / COMIC / NOVEL / GAME）"
    )
    @GetMapping("/type/{type}")
    public Result listByType(
            @Parameter(
                    description = "ACG 产品类型",
                    example = "ANIME",
                    required = true
            )
            @PathVariable AcgProductType type) {
        return Result.success(service.listByType(type));
    }

    @Operation(
            summary = "删除 ACG 产品（软删除）",
            description = "根据 ID 软删除产品（isDeleted = true）"
    )
    @DeleteMapping("/{id}")
    public Result delete(
            @Parameter(description = "产品 ID", required = true)
            @PathVariable Long id) {
        return service.delete(id)
                ? Result.success("删除成功")
                : Result.error("数据不存在");
    }

    @Operation(
            summary = "根据 ID 查询 ACG 产品",
            description = "供评分、评论等服务通过 Feign 调用，校验产品是否存在"
    )
    @GetMapping("/{id}")
    public Result getById(
            @Parameter(description = "产品 ID", required = true)
            @PathVariable Long id) {

        AcgProduct product = service.getById(id);
        return product != null
                ? Result.success(product)
                : Result.error("产品不存在");
    }


    @Operation(
            summary = "更新产品评分",
            description = "由评分服务调用，用于更新产品的平均评分"
    )
    @PutMapping("/{id}/score")
    public Result updateScore(
            @Parameter(description = "产品 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "新的平均评分", required = true)
            @RequestParam Double score) {

        return service.updateScore(id, score)
                ? Result.success("评分更新成功")
                : Result.error("产品不存在");
    }

}
