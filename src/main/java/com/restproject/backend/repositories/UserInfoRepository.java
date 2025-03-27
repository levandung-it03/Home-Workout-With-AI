package com.restproject.backend.repositories;

import com.restproject.backend.annotations.dev.Overload;
import com.restproject.backend.dtos.request.UserInfoAndStatusRequest;
import com.restproject.backend.dtos.response.UserInfoAndStatusResponse;
import com.restproject.backend.entities.UserInfo;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    @Query("""
        SELECT new com.restproject.backend.dtos.response.UserInfoAndStatusResponse(
            u.userInfoId, u.firstName, u.lastName, u.gender, u.user.email, u.coins, u.dob, u.user.userId, u.user.active,
            u.user.createdTime
        ) FROM UserInfo u
    """)
    Page<UserInfoAndStatusResponse> findAllUserInfoAndStatus(Pageable pageableCof);

    @Overload
    @Query("""
        SELECT new com.restproject.backend.dtos.response.UserInfoAndStatusResponse(
            u.userInfoId, u.firstName, u.lastName, u.gender, u.user.email, u.coins, u.dob, u.user.userId, u.user.active,
            u.user.createdTime
        ) FROM UserInfo u
        WHERE (:#{#filterObj.firstName} IS NULL OR u.firstName LIKE CONCAT('%',:#{#filterObj.firstName},'%'))
        AND (:#{#filterObj.active} IS NULL OR u.user.active = :#{#filterObj.active})
        AND (:#{#filterObj.lastName} IS NULL OR u.lastName LIKE CONCAT('%',:#{#filterObj.lastName},'%'))
        AND (:#{#filterObj.email} IS NULL   OR u.user.email LIKE CONCAT('%',:#{#filterObj.email},'%'))
        AND (:#{#filterObj.gender} IS NULL  OR u.gender = :#{#filterObj.gender})
        AND (:#{#filterObj.fromCoins} IS NULL   OR :#{#filterObj.fromCoins} <= u.coins)
        AND (:#{#filterObj.toCoins} IS NULL   OR u.coins <= :#{#filterObj.fromCoins})
        AND (:#{#filterObj.fromDob} IS NULL OR :#{#filterObj.fromDob} <= u.dob)
        AND (:#{#filterObj.toDob} IS NULL   OR u.dob <= :#{#filterObj.toDob})
        AND (:#{#filterObj.fromCreatedTime} IS NULL OR :#{#filterObj.fromCreatedTime} <= u.user.createdTime)
        AND (:#{#filterObj.toCreatedTime} IS NULL   OR u.user.createdTime <= :#{#filterObj.toCreatedTime})
    """)
    Page<UserInfoAndStatusResponse> findAllUserInfoAndStatus(@Param("filterObj") UserInfoAndStatusRequest request,
                                                             Pageable pageableCof);

    Optional<UserInfo> findByUserEmail(String subject);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM UserInfo u WHERE u.userInfoId = :userInfoId OR u.user.email = :email")
    Optional<UserInfo> findByIdOrEmailWithLock(@Param("userInfoId") Long userInfoId, @Param("email") String email);

    @Modifying
    @Transactional
    @Query("""
        UPDATE UserInfo u SET
            u.firstName = :#{#updatedObj.firstName},
            u.lastName = :#{#updatedObj.lastName},
            u.dob = :#{#updatedObj.dob},
            u.gender = :#{#updatedObj.gender}
        WHERE u.userInfoId = :#{#updatedObj.userInfoId}
    """)
    void updateUserInfoByUserInfoId(@Param("updatedObj") UserInfo userInfo);
}
