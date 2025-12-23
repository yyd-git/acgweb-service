package com.zjsu.yyd.acgproductservice.repository;

import com.zjsu.yyd.acgproductservice.model.AcgResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcgResourceRepository extends JpaRepository<AcgResource, Long> {
    List<AcgResource> findByProductIdAndIsDeletedFalse(Long productId);
}
