package com.zjsu.yyd.userservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户名 */
    @Column(nullable = false, unique = true)
    private String userName;

    /** 加密后的密码 */
    @Column(nullable = false)
    private String userPassword;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /** 软删除标志 */
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public User() {
        this.createTime = LocalDateTime.now();
    }
}
