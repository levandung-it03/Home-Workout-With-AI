package com.restproject.backend.repositories;

import com.restproject.backend.annotations.dev.Overload;
import com.restproject.backend.dtos.request.UserInfoAndStatusRequest;
import com.restproject.backend.dtos.response.UserInfoAndStatusResponse;
import com.restproject.backend.entities.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    @Query("""
        SELECT new com.restproject.backend.dtos.response.UserInfoAndStatusResponse(
            u.userInfoId, u.firstName, u.lastName, u.gender, us.email, u.coins, u.dob, u.user.userId, u.user.active,
            us.createdTime
        ) FROM UserInfo u INNER JOIN User us ON u.user.userId = us.userId
    """)
    Page<UserInfoAndStatusResponse> findAllUserInfoAndStatus(Pageable pageableCof);

    @Overload
    @Query("""
        SELECT new com.restproject.backend.dtos.response.UserInfoAndStatusResponse(
            u.userInfoId, u.firstName, u.lastName, u.gender, us.email, u.coins, u.dob, u.user.userId, u.user.active,
            us.createdTime
        ) FROM UserInfo u INNER JOIN User us ON u.user.userId = us.userId
        WHERE (:#{#filterObj.firstName} IS NULL OR u.firstName LIKE CONCAT('%',:#{#filterObj.firstName},'%'))
        AND (:#{#filterObj.isActive} IS NULL OR u.user.active = :#{#filterObj.isActive})
        AND (:#{#filterObj.lastName} IS NULL OR u.lastName LIKE CONCAT('%',:#{#filterObj.lastName},'%'))
        AND (:#{#filterObj.email} IS NULL   OR us.email LIKE CONCAT('%',:#{#filterObj.email},'%'))
        AND (:#{#filterObj.gender} IS NULL  OR u.gender = :#{#filterObj.gender})
        AND (:#{#filterObj.coins} IS NULL   OR u.coins = :#{#filterObj.coins})
        AND (:#{#filterObj.fromDob} IS NULL OR :#{#filterObj.fromDob} <= u.dob)
        AND (:#{#filterObj.toDob} IS NULL   OR u.dob <= :#{#filterObj.toDob})
        AND (:#{#filterObj.fromCreatedTime} IS NULL OR :#{#filterObj.fromCreatedTime} <= us.createdTime)
        AND (:#{#filterObj.toCreatedTime} IS NULL   OR us.createdTime <= :#{#filterObj.toCreatedTime})
    """)
    Page<UserInfoAndStatusResponse> findAllUserInfoAndStatus(
        @Param("filterObj") UserInfoAndStatusRequest request, Pageable pageableCof);

    Optional<UserInfo> findByUserEmail(String subject);
}
