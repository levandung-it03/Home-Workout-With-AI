package com.restproject.backend.services;

import com.restproject.backend.dtos.request.NewUserInfoRequest;
import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.request.UpdateUserInfoRequest;
import com.restproject.backend.dtos.request.UserInfoAndStatusRequest;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.dtos.response.UserInfoAndStatusResponse;
import com.restproject.backend.entities.Auth.User;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.mappers.UserInfoMappers;
import com.restproject.backend.repositories.UserInfoRepository;
import com.restproject.backend.repositories.UserRepository;
import com.restproject.backend.services.Auth.JwtService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserInfoMappers userInfoMappers;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final JwtService jwtService;
    private final PageMappers pageMappers;

    @Value("${services.back-end.user-info.default-coins}")
    private int defaultCoins;

    public UserInfo registerUserInfo(NewUserInfoRequest request, String accessToken) throws ApplicationException {
        UserInfo repoReq = userInfoMappers.insertionToPlain(request);
        User userByToken = userRepository.findByUsername(jwtService.readPayload(accessToken).get("subject"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));//--Never throw by validated token
        repoReq.setUser(userByToken);
        repoReq.setCoins((long) defaultCoins);    //--Default coins for new User.
        return userInfoRepository.save(repoReq);    //--FetchType.LAZY will ignore User
    }

    public TablePagesResponse<UserInfoAndStatusResponse> getUserInfoAndStatusPages(PaginatedTableRequest request) {
        if (!Objects.isNull(request.getSortedField())   //--If it's null, it means client doesn't want to sort.
        && !UserInfoAndStatusRequest.INSTANCE_FIELDS.contains(request.getSortedField()))
            throw new ApplicationException(ErrorCodes.INVALID_SORTING_FIELD_OR_VALUE);
        Pageable pageableCof = pageMappers.tablePageRequestToPageable(request).toPageable();

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
        var updatedUserInfo = userInfoRepository.findByUserUsername(jwtService.readPayload(accessToken).get("subject"))
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));                
        userInfoMappers.updateTarget(updatedUserInfo, request);
        userInfoRepository.deleteById(updatedUserInfo.getUserInfoId());
        updatedUserInfo.setUserInfoId(null);
        return userInfoRepository.save(updatedUserInfo);    //--FetchType.LAZY will ignore User
    }
}
