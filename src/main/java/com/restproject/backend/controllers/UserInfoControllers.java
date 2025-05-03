package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.FullChangingCoinsResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.dtos.response.UserInfoAndStatusResponse;
import com.restproject.backend.entities.ChangingCoinsHistories;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/private")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "Bearer", bearerFormat = "JWT")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserInfoControllers {
    UserInfoService userInfoService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user information")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=26002; message=Get User Info pages successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10008,10009]; message=Invalid [filtering,sorting] field or value",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping("/admin/v1/get-user-info-and-status-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<UserInfoAndStatusResponse>>> getUserInfoAndStatusPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_USER_INFO_PAGES,
            userInfoService.getUserInfoAndStatusPages(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get changing coins histories of user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=30004; message=Get Coins history successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10008,10009]; message=Invalid [filtering,sorting] field or value",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @GetMapping("/admin/v1/get-changing-coins-histories-of-user")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<FullChangingCoinsResponse>>>
    getAllChangingCoinsHistoriesOfUser(@Valid PaginatedRelationshipRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_COINS_HISTORIES,
            userInfoService.getAllChangingCoinsHistoriesOfUser(request));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update user information")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=26002; message=Get User Info pages successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "401",
            description = "code=11002; message=Token or its claims are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @ResponseBody
    @PutMapping("/user/v1/update-user-info")
    public ResponseEntity<ApiResponseObject<UserInfo>> updateUserInfo(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody UpdateUserInfoRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_USER_INFO,
            userInfoService.updateUserInfo(request, accessToken));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user information")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=26004; message=Get User Info successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "401",
            description = "code=11002; message=Token or its claims are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @ResponseBody
    @GetMapping("/user/v1/get-info")
    public ResponseEntity<ApiResponseObject<UserInfo>> getInfo(@RequestHeader("Authorization") String accessToken) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_USER_INFO, userInfoService.getInfo(accessToken));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get changing coins histories of user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=30004; message=Get Coins history successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "401",
            description = "code=11002; message=Token or its claims are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @GetMapping("/user/v1/get-changing-coins-histories-of-user")
    public ResponseEntity<ApiResponseObject<List<ChangingCoinsHistories>>> getChangingCoinsHistoriesOfUser(
        @RequestHeader("Authorization") String accessToken) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_COINS_HISTORIES,
            userInfoService.getChangingCoinsHistoriesOfUser(accessToken));
    }
}
