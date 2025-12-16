package com.zjsu.yyd.acgratingservice.repository;

import com.zjsu.yyd.acgratingservice.model.AcgRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AcgRatingRepository extends JpaRepository<AcgRating, Long> {

    Optional<AcgRating> findByUserIdAndProductIdAndIsDeletedFalse(Long userId, Long productId);

    @Query("SELECT AVG(r.score) FROM AcgRating r WHERE r.productId = :productId AND r.isDeleted = false")
    Double calculateAvgScore(Long productId);
}
