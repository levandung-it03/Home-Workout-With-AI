package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.SEPayWebHookRequest;
import com.restproject.backend.dtos.request.SePayQrUrlRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.ThirdParty.SEPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SePayControllers {
    SEPayService sePayService;

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get QR URL generated from Owner Bank Account")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=30001; message=Get QR URL successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=11002; message=Token or its claims are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping("/api/private/user/v1/get-sepay-qr-url")
    public ResponseEntity<ApiResponseObject<Map<String, String>>> getSePayQrUrl(
        @RequestHeader("Authorization") String accessToken, @Valid SePayQrUrlRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SEPAY_QR_URL,
            sePayService.getSePayQrUrl(request, accessToken));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Simple controller for Client's Device, to interval checking if trans has been completed successfully")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=30002; message=Deposit Coins successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "200",
            description = "code=30003; message=Deposit progress is processing, please wait!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=11002; message=Token or its claims are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping("/api/private/user/v1/get-deposit-status")
    public ResponseEntity<ApiResponseObject<Void>> checkDepositStatus(
        @RequestHeader("Authorization") String accessToken, @Valid String description) {
        return sePayService.checkDepositStatus(description, accessToken)
            ? ApiResponseObject.buildSuccessResponse(SucceedCodes.DEPOSIT_COINS)
            : ApiResponseObject.buildSuccessResponse(SucceedCodes.PROCESSING_DEPOSIT_BANKING);
    }

    @Operation(summary = "Webhook controller (a passive or reactive controller)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Simple response to SePay server to notice a successful trans",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/api/public/user/sepay/v1/webhook")
    public ResponseEntity<String> webhook(@RequestBody SEPayWebHookRequest request) {
        sePayService.checkDescriptionAndAccumulateCoins(request);
        return ResponseEntity.ok("Money Received.");
    }
}
