package com.restproject.backend.services;

import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.request.UpdateUserInfoRequest;
import com.restproject.backend.dtos.request.UserInfoAndStatusRequest;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.dtos.response.UserInfoAndStatusResponse;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.mappers.UserInfoMappers;
import com.restproject.backend.repositories.UserInfoRepository;
import com.restproject.backend.services.Auth.JwtService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserInfoService {
    UserInfoMappers userInfoMappers;
    UserInfoRepository userInfoRepository;
    JwtService jwtService;
    PageMappers pageMappers;

    public TablePagesResponse<UserInfoAndStatusResponse> getUserInfoAndStatusPages(PaginatedTableRequest request) {
        Pageable pageableCof = pageMappers.tablePageRequestToPageable(request).toPageable(UserInfo.class);

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            var repoRes = userInfoRepository.findAllUserInfoAndStatus(pageableCof);
            return TablePagesResponse.<UserInfoAndStatusResponse>builder().totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage()).data(repoRes.stream().toList()).build();
        }

        UserInfoAndStatusRequest filterObj;
        try {
            filterObj = UserInfoAndStatusRequest.buildFromHashMap(request.getFilterFields());
        } catch (ApplicationException | IllegalArgumentException | NullPointerException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }

        Page<UserInfoAndStatusResponse> repoRes = userInfoRepository.findAllUserInfoAndStatus(filterObj, pageableCof);
        return TablePagesResponse.<UserInfoAndStatusResponse>builder().totalPages(repoRes.getTotalPages())
            .currentPage(repoRes.getTotalPages()).data(repoRes.stream().toList()).build();
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public UserInfo updateUserInfo(UpdateUserInfoRequest request, String accessToken) {
        var updatedUserInfo = userInfoRepository.findByUserEmail(jwtService.readPayload(accessToken).get("subject"))
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));                
        userInfoMappers.updateTarget(updatedUserInfo, request);
        userInfoRepository.deleteById(updatedUserInfo.getUserInfoId());
        updatedUserInfo.setUserInfoId(null);
        return userInfoRepository.save(updatedUserInfo);    //--FetchType.LAZY will ignore User
    }
}
