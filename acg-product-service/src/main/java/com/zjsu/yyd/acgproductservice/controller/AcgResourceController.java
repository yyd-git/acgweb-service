package com.zjsu.yyd.acgproductservice.controller;

import com.zjsu.yyd.acgproductservice.model.AcgResource;
import com.zjsu.yyd.acgproductservice.model.Result;
import com.zjsu.yyd.acgproductservice.service.AcgResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/acg-resource")
@Tag(name = "ACG 资源管理")
public class AcgResourceController {

    @Autowired
    private AcgResourceService resourceService;

    /**
     * 上传资源（支持 zip / rar）
     */
    @PostMapping(
            value = "/upload",
            consumes = "multipart/form-data"
    )
    @Operation(
            summary = "上传资源",
            description = "上传 zip / rar 等压缩资源文件"
    )
    public Result uploadResource(
            @Parameter(description = "资源压缩包文件", required = true)
            @RequestPart("file") MultipartFile file,

            @Parameter(description = "上传用户ID", required = true, example = "1")
            @RequestParam("userId") Long userId,

            @Parameter(description = "资源名称", required = true, example = "进击的巨人全集")
            @RequestParam("name") String name,

            @Parameter(description = "资源介绍", example = "动漫全集压缩包")
            @RequestParam(value = "description", required = false) String description,

            @Parameter(description = "所属产品ID", required = true, example = "1")
            @RequestParam("productId") Long productId
    ) {
        try {
            AcgResource resource = resourceService.uploadResource(
                    file,
                    userId,
                    name,
                    description,
                    productId
            );
            return Result.success(resource); // ✅ 返回 Result 包装
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 下载资源
     */
    @GetMapping("/download/{id}")
    @Operation(summary = "下载资源")
    public void downloadResource(
            @PathVariable("id") Long id,
            HttpServletResponse response
    ) throws IOException {

        Optional<AcgResource> opt = resourceService.getResource(id);
        if (opt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "资源不存在");
            return;
        }

        AcgResource resource = opt.get();
        File file = resourceService.getResourceFile(resource);

        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }

        response.setContentType("application/octet-stream");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=" +
                        URLEncoder.encode(file.getName(), StandardCharsets.UTF_8)
        );

        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }

    /**
     * 根据产品 ID 获取全部资源列表
     */
    @GetMapping("/list/{productId}")
    @Operation(summary = "根据产品 ID 获取全部资源", description = "返回指定产品下的全部资源列表（未删除）")
    public Result listResourcesByProduct(
            @Parameter(description = "产品 ID", required = true)
            @PathVariable Long productId
    ) {
        try {
            return Result.success(resourceService.listByProductId(productId));
        } catch (Exception e) {
            return Result.error("获取资源列表失败：" + e.getMessage());
        }
    }
}

