package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.SEPayWebHookRequest;
import com.restproject.backend.dtos.request.SePayQrUrlRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.ThirdParty.SEPayService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SePayControllers {
    SEPayService sePayService;

    @ResponseBody
    @GetMapping("/api/private/user/v1/get-sepay-qr-url")
    public ResponseEntity<ApiResponseObject<Map<String, String>>> getSePayQrUrl(
        @RequestHeader("Authorization") String accessToken, @Valid SePayQrUrlRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SEPAY_QR_URL,
            sePayService.getSePayQrUrl(request, accessToken));
    }

    @ResponseBody
    @GetMapping("/api/private/user/v1/get-deposit-status")
    public ResponseEntity<ApiResponseObject<Void>> checkDepositStatus(
        @RequestHeader("Authorization") String accessToken, @Valid String description) {
        return sePayService.checkDepositStatus(description, accessToken)
            ? ApiResponseObject.buildSuccessResponse(SucceedCodes.DEPOSIT_COINS)
            : ApiResponseObject.buildSuccessResponse(SucceedCodes.PROCESSING_DEPOSIT_BANKING);
    }

    @ResponseBody
    @PostMapping("/api/public/user/sepay/v1/webhook")
    public ResponseEntity<String> webhook(@RequestBody SEPayWebHookRequest request) {
        sePayService.checkDescriptionAndAccumulateCoins(request);
        return ResponseEntity.ok("Money Received.");
    }
}
