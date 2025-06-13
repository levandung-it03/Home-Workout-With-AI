package com.restproject.backend.repositories;

import com.restproject.backend.dtos.request.FullChangingCoinsRequest;
import com.restproject.backend.dtos.response.FullChangingCoinsResponse;
import com.restproject.backend.entities.ChangingCoinsHistories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChangingCoinsHistoriesRepository extends JpaRepository<ChangingCoinsHistories, String> {
    boolean existsByUserInfoUserInfoIdAndDescription(Long userInfoId, String desc);

    @Query("SELECT h FROM ChangingCoinsHistories h WHERE h.userInfo.userInfoId = :userInfoId")
    List<ChangingCoinsHistories> findTop20ByUserInfoUserInfoId(Pageable pageable, @Param("userInfoId") Long userInfoId);

    @Query("""
        SELECT c.changingCoinsHistoriesId, c.changingCoins, c.changingTime, c.changingCoinsType,
            CONCAT(c.userInfo.firstName, ' ', c.userInfo.lastName) AS fullName
        FROM ChangingCoinsHistories c WHERE c.userInfo.userInfoId = :id
        ORDER BY c.changingTime DESC
    """)
    Page<Object[]> findAllByUserInfoUserInfoId(@Param("id") Long id, Pageable pageableCfg);

    @Query("""
        SELECT c.changingCoinsHistoriesId, c.changingCoins, c.changingTime, c.changingCoinsType,
            CONCAT(c.userInfo.firstName, ' ', c.userInfo.lastName) AS fullName
        FROM ChangingCoinsHistories c WHERE (c.userInfo.userInfoId = :id)
        AND (:#{#filterObj.changingCoinsHistoriesId} IS NULL OR c.changingCoinsHistoriesId = :#{#filterObj.changingCoinsHistoriesId})
        AND (:#{#filterObj.fullName} IS NULL OR CONCAT(c.userInfo.firstName, ' ', c.userInfo.lastName) LIKE CONCAT('%',:#{#filterObj.fullName},'%'))
        AND (:#{#filterObj.changingCoins} IS NULL OR c.changingCoins = :#{#filterObj.changingCoins})
        AND (:#{#filterObj.changingCoinsType} IS NULL OR c.changingCoinsType = :#{#filterObj.changingCoinsType})
        AND (:#{#filterObj.fromChangingTime} IS NULL OR :#{#filterObj.fromChangingTime} <= c.changingTime)
        AND (:#{#filterObj.toChangingTime} IS NULL OR c.changingTime <= :#{#filterObj.toChangingTime})
        ORDER BY c.changingTime DESC
    """)
    Page<Object[]> findAllByUserInfoUserInfoId(
        @Param("id") Long id,
        @Param("filterObj") FullChangingCoinsRequest filterObj,
        Pageable pageableCfg
    );
}
