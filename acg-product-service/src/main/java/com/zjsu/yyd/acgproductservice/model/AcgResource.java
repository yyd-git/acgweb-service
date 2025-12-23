package com.zjsu.yyd.acgproductservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "acg_resource")
@Schema(description = "ACG 产品资源实体")
public class AcgResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "资源ID", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "上传用户ID", example = "1001")
    private Long userId;

    @Column(nullable = false)
    @Schema(description = "资源名称", example = "进击的巨人全集")
    private String name;

    @Column(length = 1000)
    @Schema(description = "资源介绍", example = "进击的巨人动漫全集资源")
    private String description;

    @Column(nullable = false)
    @Schema(description = "所属产品ID", example = "1")
    private Long productId;

    @Column(nullable = false)
    @Schema(description = "资源存储路径", example = "resource/aot.zip")
    private String resourcePath;

    @Column(nullable = false)
    @Schema(description = "创建时间", example = "2025-06-01T10:30:00")
    private LocalDateTime createTime;

    @Column(nullable = false)
    @Schema(description = "是否已删除（软删除）", example = "false")
    private Boolean isDeleted = false;

    public AcgResource() {
        this.createTime = LocalDateTime.now();
    }
}
