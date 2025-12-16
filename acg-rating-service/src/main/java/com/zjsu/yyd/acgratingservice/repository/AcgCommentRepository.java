package com.zjsu.yyd.acgratingservice.repository;

import com.zjsu.yyd.acgratingservice.model.AcgComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcgCommentRepository extends JpaRepository<AcgComment, Long> {

    List<AcgComment> findByProductIdAndIsDeletedFalse(Long productId);
}
