package com.zjsu.yyd.acgratingservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "acg_rating",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "productId"})
        }
)
public class AcgRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID */
    @Column(nullable = false)
    private Long userId;

    /** 产品ID */
    @Column(nullable = false)
    private Long productId;

    /** 评分（1~10） */
    @Column(nullable = false)
    private Integer score;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /** 软删除 */
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public AcgRating() {
        this.createTime = LocalDateTime.now();
    }
}
