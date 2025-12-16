package com.zjsu.yyd.acgproductservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "acg_product")
@Schema(description = "ACG 产品实体（动漫 / 漫画 / 轻小说 / 游戏）")
public class AcgProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "产品ID", example = "1")
    private Long id;

    /** 名称 */
    @Column(nullable = false)
    @Schema(description = "产品名称", example = "进击的巨人")
    private String name;

    /** 简介 */
    @Column(length = 1000)
    @Schema(description = "产品简介", example = "人类与巨人的生存之战")
    private String description;

    /** 产品类型 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(
            description = "产品类型",
            example = "ANIME",
            allowableValues = {"ANIME", "COMIC", "NOVEL", "GAME"}
    )
    private AcgProductType type;

    /** 封面本地路径 */
    @Schema(description = "封面图片本地路径", example = "/images/aot.jpg")
    private String coverPath;

    /** 总评分 */
    @Column(nullable = false)
    @Schema(description = "总评分", example = "9.5")
    private Double totalScore = 0.0;

    /** 是否存在资源 */
    @Column(nullable = false)
    @Schema(description = "是否存在资源", example = "false")
    private Boolean hasResource = false;

    // ===== 动漫 =====
    @Schema(description = "动画制作公司（仅动漫适用）", example = "MAPPA")
    private String studio;

    @Schema(description = "集数（仅动漫适用）", example = "24")
    private Integer episodeCount;

    // ===== 漫画 / 轻小说 =====
    @Schema(description = "作者（漫画 / 轻小说适用）", example = "谏山创")
    private String author;

    @Schema(description = "章节数（漫画 / 轻小说适用）", example = "139")
    private Integer chapterCount;

    @Schema(description = "卷数（漫画 / 轻小说适用）", example = "34")
    private Integer volumeCount;

    // ===== 游戏 =====
    @Schema(description = "游戏开发商（仅游戏适用）", example = "FromSoftware")
    private String developer;

    @Schema(description = "游戏平台（仅游戏适用）", example = "PC / PS5")
    private String platform;

    /** 创建时间 */
    @Column(nullable = false)
    @Schema(description = "创建时间", example = "2025-06-01T10:30:00")
    private LocalDateTime createTime;

    /** 软删除 */
    @Column(nullable = false)
    @Schema(description = "是否已删除（软删除）", example = "false")
    private Boolean isDeleted = false;

    public AcgProduct() {
        this.createTime = LocalDateTime.now();
    }
}
