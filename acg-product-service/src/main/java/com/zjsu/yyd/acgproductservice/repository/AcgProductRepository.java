package com.zjsu.yyd.acgproductservice.repository;

import com.zjsu.yyd.acgproductservice.model.AcgProduct;
import com.zjsu.yyd.acgproductservice.model.AcgProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcgProductRepository extends JpaRepository<AcgProduct, Long> {

    List<AcgProduct> findByIsDeletedFalse();

    // 注意这里是 AcgProductType，不是 String
    List<AcgProduct> findByTypeAndIsDeletedFalse(AcgProductType type);
}
