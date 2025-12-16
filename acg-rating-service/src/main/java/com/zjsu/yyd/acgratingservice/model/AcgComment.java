package com.zjsu.yyd.acgratingservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "acg_comment")
public class AcgComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID */
    @Column(nullable = false)
    private Long userId;

    /** 产品ID */
    @Column(nullable = false)
    private Long productId;

    /** 评论内容 */
    @Column(nullable = false, length = 1000)
    private String content;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /** 软删除 */
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public AcgComment() {
        this.createTime = LocalDateTime.now();
    }
}
