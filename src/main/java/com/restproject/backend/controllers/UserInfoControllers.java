package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.FullChangingCoinsRequest;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.FullChangingCoinsResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.dtos.response.UserInfoAndStatusResponse;
import com.restproject.backend.entities.ChangingCoinsHistories;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.UserInfoService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserInfoControllers {
    UserInfoService userInfoService;

    @ResponseBody
    @GetMapping("/admin/v1/get-user-info-and-status-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<UserInfoAndStatusResponse>>> getUserInfoAndStatusPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_USER_INFO_PAGES,
            userInfoService.getUserInfoAndStatusPages(request));
    }

    @ResponseBody
    @GetMapping("/user/v1/get-info")
    public ResponseEntity<ApiResponseObject<UserInfo>> getInfo(@RequestHeader("Authorization") String accessToken) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_USER_INFO, userInfoService.getInfo(accessToken));
    }

    @ResponseBody
    @PutMapping("/user/v1/update-user-info")
    public ResponseEntity<ApiResponseObject<UserInfo>> updateUserInfo(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody UpdateUserInfoRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_USER_INFO,
            userInfoService.updateUserInfo(request, accessToken));
    }

    @GetMapping("/user/v1/get-changing-coins-histories-of-user")
    public ResponseEntity<ApiResponseObject<List<ChangingCoinsHistories>>> getChangingCoinsHistoriesOfUser(
        @RequestHeader("Authorization") String accessToken) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_COINS_HISTORIES,
            userInfoService.getChangingCoinsHistoriesOfUser(accessToken));
    }

    @GetMapping("/admin/v1/get-changing-coins-histories-of-user")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<FullChangingCoinsResponse>>>
    getAllChangingCoinsHistoriesOfUser(@Valid PaginatedRelationshipRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_COINS_HISTORIES,
            userInfoService.getAllChangingCoinsHistoriesOfUser(request));
    }
}
