package com.zjsu.yyd.acgproductservice.repository;

import com.zjsu.yyd.acgproductservice.model.AcgProduct;
import com.zjsu.yyd.acgproductservice.model.AcgProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.query.Param;

public interface AcgProductRepository extends JpaRepository<AcgProduct, Long> {

    List<AcgProduct> findByIsDeletedFalse();

    // 注意这里是 AcgProductType，不是 String
    List<AcgProduct> findByTypeAndIsDeletedFalse(AcgProductType type);

    // 分页 + 名称模糊 + 类型可选
    @Query("SELECT p FROM AcgProduct p WHERE p.isDeleted = false " +
            "AND (:type IS NULL OR p.type = :type) " +
            "AND (:name IS NULL OR p.name LIKE %:name%)")
    Page<AcgProduct> findByNameAndType(
            @Param("name") String name,
            @Param("type") AcgProductType type,
            Pageable pageable
    );
}
