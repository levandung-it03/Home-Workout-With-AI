package com.restproject.backend.services.ThirdParty;

import com.restproject.backend.dtos.request.SEPayWebHookRequest;
import com.restproject.backend.dtos.request.SePayQrUrlRequest;
import com.restproject.backend.entities.ChangingCoinsHistories;
import com.restproject.backend.enums.ChangingCoinsType;
import com.restproject.backend.enums.DefaultSEPayParams;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.ChangingCoinsHistoriesRepository;
import com.restproject.backend.repositories.UserInfoRepository;
import com.restproject.backend.services.Auth.JwtService;
import com.restproject.backend.services.SubscriptionService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SEPayService {
    @Value("${sepay.target.description}")
    private String DEFAULT_DESCRIPTION;
    @Value("${sepay.service.create-qr-url}")
    private String qrGeneratingUrl;
    @Value("${sepay.target.account}")
    private String accountTarget;
    @Value("${sepay.target.bank-name}")
    private String bankName;

    private final SubscriptionService subscriptionService;
    private final UserInfoRepository userInfoRepository;
    private final ChangingCoinsHistoriesRepository changingCoinsHistoriesRepository;
    private final JwtService jwtService;

    public Map<String, String> getSePayQrUrl(@Valid SePayQrUrlRequest request, String accessToken) {
        var userInfo = userInfoRepository
            .findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));
        var description = userInfo.getUserInfoId().toString() + DEFAULT_DESCRIPTION + System.currentTimeMillis();
        var urlResult = qrGeneratingUrl.replace(DefaultSEPayParams.ACCOUNT_TARGET.getValue(), accountTarget);
        urlResult = urlResult.replace(DefaultSEPayParams.AMOUNT.getValue(), request.getCoinsAmount().toString());
        urlResult = urlResult.replace(DefaultSEPayParams.DESCRIPTION.getValue(), description);
        urlResult = urlResult.replace(DefaultSEPayParams.BANK_TARGET.getValue(), bankName);
        return Map.of("url", urlResult, "accountTarget", accountTarget, "bankName", bankName,
            "description", description);
    }

    @Transactional(rollbackOn = RuntimeException.class)
    public void checkDescriptionAndAccumulateCoins(SEPayWebHookRequest request) {
        try {
            if (changingCoinsHistoriesRepository.existsById(request.getReferenceCode()))
                throw new ApplicationException(ErrorCodes.DUPLICATE_DEPOSIT_BANKING);
            var trueDescription = Arrays.stream(request.getDescription().split(" "))
                .filter(chars -> chars.contains(DEFAULT_DESCRIPTION))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_BANKING_DESCRIPTION));
            var userInfoId = Long.parseLong(trueDescription.split(DEFAULT_DESCRIPTION)[0]);
            var userInfo = this.subscriptionService.getUserInfoToUpdateCoinsWithLock(userInfoId, null);
            var depositedCoins = (long) Math.floor((double) request.getTransferAmount() / 1000);
            userInfo.setCoins(userInfo.getCoins() + depositedCoins);

            userInfoRepository.save(userInfo);
            changingCoinsHistoriesRepository.save(ChangingCoinsHistories.builder()
                .changingCoinsHistoriesId(request.getReferenceCode())
                .userInfo(userInfo)
                .description(trueDescription)
                .changingCoins(depositedCoins)
                .changingTime(LocalDateTime.now())
                .changingCoinsType(ChangingCoinsType.DEPOSIT)
                .build());
        } catch (NullPointerException e) {
            log.info("Exception thrown by SEPayService.checkDescriptionAndAccumulateCoins: {}", e);
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);
        }
    }

    public boolean checkDepositStatus(String desc, String accessToken) {
        var userInfo = userInfoRepository
            .findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));
        return changingCoinsHistoriesRepository.existsByUserInfoUserInfoIdAndDescription(userInfo.getUserInfoId(), desc);
    }
}
