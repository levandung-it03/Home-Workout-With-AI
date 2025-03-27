package com.restproject.backend.services;

import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.dtos.response.UserInfoAndStatusResponse;
import com.restproject.backend.entities.ChangingCoinsHistories;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.mappers.UserInfoMappers;
import com.restproject.backend.repositories.ChangingCoinsHistoriesRepository;
import com.restproject.backend.repositories.UserInfoRepository;
import com.restproject.backend.services.Auth.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserInfoService {
    UserInfoMappers userInfoMappers;
    UserInfoRepository userInfoRepository;
    ChangingCoinsHistoriesRepository changingCoinsHistoriesRepository;
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

    public UserInfo updateUserInfo(UpdateUserInfoRequest request, String accessToken) {
        var updatedUserInfo = userInfoRepository.findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));                
        userInfoMappers.updateTarget(updatedUserInfo, request);
        userInfoRepository.updateUserInfoByUserInfoId(updatedUserInfo);
        return updatedUserInfo;
    }

    public UserInfo getInfo(String accessToken) {
        return userInfoRepository.findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.FORBIDDEN_USER));
    }

    public List<ChangingCoinsHistories> getChangingCoinsHistoriesOfUser(String accessToken) {
        var userInfo = userInfoRepository
            .findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));
        Pageable pageable = PageRequest.of(0, 20);  //--Just get the first 20 lines of histories.
        var result = changingCoinsHistoriesRepository.findAllByUserInfoUserInfoId(pageable, userInfo.getUserInfoId());
        for (int index = 0; index < result.size(); index++) {   //--Hiding private fields.
            result.get(index).setChangingCoinsHistoriesId(Integer.toString(index));
            result.get(index).setDescription(null);
            result.get(index).setUserInfo(null);
        }
        return result;
    }


}