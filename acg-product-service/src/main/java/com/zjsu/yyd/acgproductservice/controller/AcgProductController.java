package com.zjsu.yyd.acgproductservice.controller;

import com.zjsu.yyd.acgproductservice.model.AcgProduct;
import com.zjsu.yyd.acgproductservice.model.AcgProductType;
import com.zjsu.yyd.acgproductservice.model.Result;
import com.zjsu.yyd.acgproductservice.service.AcgProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/acg-product")
@Tag(name = "ACG 产品管理接口", description = "用于管理动漫 / 漫画 / 轻小说 / 游戏等 ACG 产品")
public class AcgProductController {

    private final AcgProductService service;

    public AcgProductController(AcgProductService service) {
        this.service = service;
    }

//    @Operation(
//            summary = "新增 ACG 产品",
//            description = "新增一个 ACG 产品（动漫 / 漫画 / 轻小说 / 游戏），默认未删除"
//    )
//    @PostMapping
//    public Result add(
//            @Parameter(description = "ACG 产品对象", required = true)
//            @RequestBody AcgProduct product) {
//        return Result.success(service.add(product));
//    }

//    @PostMapping
//    @Operation(summary = "新增 ACG 产品", description = "新增一个 ACG 产品（可上传封面图片）")
//    public Result add(
//            @Parameter(description = "ACG 产品对象", required = true)
//            @RequestPart("product") AcgProduct product,
//            @Parameter(description = "封面图片文件")
//            @RequestPart(value = "coverFile", required = false) MultipartFile coverFile
//    ) {
//        try {
//            if (coverFile != null && !coverFile.isEmpty()) {
//                String coverPath = service.saveCover(coverFile);
//                product.setCoverPath(coverPath);
//            }
//            return Result.success(service.add(product));
//        } catch (Exception e) {
//            return Result.error("上传封面失败：" + e.getMessage());
//        }
//    }

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "新增 ACG 产品", description = "新增一个 ACG 产品（可上传封面图片）")
    public Result add(
            @Parameter(description = "产品名称", required = true) @RequestParam String name,
            @Parameter(description = "产品简介") @RequestParam(required = false) String description,
            @Parameter(description = "产品类型", required = true, example = "ANIME") @RequestParam AcgProductType type,
            @Parameter(description = "动画制作公司") @RequestParam(required = false) String studio,
            @Parameter(description = "集数") @RequestParam(required = false) Integer episodeCount,
            @Parameter(description = "作者") @RequestParam(required = false) String author,
            @Parameter(description = "章节数") @RequestParam(required = false) Integer chapterCount,
            @Parameter(description = "卷数") @RequestParam(required = false) Integer volumeCount,
            @Parameter(description = "游戏开发商") @RequestParam(required = false) String developer,
            @Parameter(description = "游戏平台") @RequestParam(required = false) String platform,
            @Parameter(description = "封面图片文件") @RequestPart(value = "coverFile", required = false) MultipartFile coverFile
    ) {
        try {
            AcgProduct product = new AcgProduct();
            product.setName(name);
            product.setDescription(description);
            product.setType(type);
            product.setStudio(studio);
            product.setEpisodeCount(episodeCount);
            product.setAuthor(author);
            product.setChapterCount(chapterCount);
            product.setVolumeCount(volumeCount);
            product.setDeveloper(developer);
            product.setPlatform(platform);

            if (coverFile != null && !coverFile.isEmpty()) {
                String coverPath = service.saveCover(coverFile);
                product.setCoverPath(coverPath);
            }

            return Result.success(service.add(product));
        } catch (Exception e) {
            return Result.error("上传封面失败：" + e.getMessage());
        }
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

    @Operation(summary = "分页查询 ACG 产品", description = "支持名称模糊查询 + 类型筛选")
    @GetMapping("/page")
    public Result listByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) AcgProductType type
    ) {
        return Result.success(service.listByPage(page, size, name, type));
    }

}
