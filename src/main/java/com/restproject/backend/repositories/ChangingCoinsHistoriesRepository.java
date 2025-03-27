package com.restproject.backend.repositories;

import com.restproject.backend.entities.ChangingCoinsHistories;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChangingCoinsHistoriesRepository extends JpaRepository<ChangingCoinsHistories, String> {
    boolean existsByUserInfoUserInfoIdAndDescription(Long userInfoId, String desc);

    @Query("SELECT h FROM ChangingCoinsHistories h WHERE h.userInfo.userInfoId = :userInfoId")
    List<ChangingCoinsHistories> findAllByUserInfoUserInfoId(Pageable pageable, @Param("userInfoId") Long userInfoId);
}
