package com.restproject.backend.services;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.PreviewScheduleResponse;
import com.restproject.backend.dtos.response.SessionToPerformResponse;
import com.restproject.backend.dtos.response.SubscriptionsInfoResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.*;
import com.restproject.backend.enums.Aim;
import com.restproject.backend.enums.ChangingCoinsType;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.repositories.ChangingCoinsHistoriesRepository;
import com.restproject.backend.repositories.ScheduleRepository;
import com.restproject.backend.repositories.SubscriptionRepository;
import com.restproject.backend.repositories.UserInfoRepository;
import com.restproject.backend.services.Auth.JwtService;
import jakarta.persistence.PessimisticLockException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionService {
    SubscriptionRepository subscriptionRepository;
    UserInfoRepository userInfoRepository;
    ScheduleRepository scheduleRepository;
    ChangingCoinsHistoriesRepository changingCoinsHistoriesRepository;
    JwtService jwtService;
    PageMappers pageMappers;

    public List<Schedule> getSchedulesOfUser(ScheduleByStatusRequest request, String accessToken) {
        return subscriptionRepository.getAllScheduleByUsernameAndStatus(
            jwtService.readPayload(accessToken).get("sub"),
            request.getIsCompleted() ? 1 : 0);
    }

    public TablePagesResponse<SubscriptionsInfoResponse> getSubscriptionsOfUserInfoPages(
        PaginatedRelationshipRequest request) {
        Pageable pageableCfg = pageMappers.relationshipPageRequestToPageable(request).toPageable(Subscription.class);

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Object[]> repoRes = subscriptionRepository
                .findAddNeededSubscriptionsInfoByUserInfoId(request.getId(), pageableCfg);
            return TablePagesResponse.<SubscriptionsInfoResponse>builder()
                .data(repoRes.stream().map(SubscriptionsInfoResponse::buildFromNativeQuery).toList())
                .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
        }

        //--Build filtering info.
        SubscriptionsInfoRequest subscriptionInfo;
        try {
            subscriptionInfo = SubscriptionsInfoRequest.buildFromHashMap(request.getFilterFields());
        } catch (ApplicationException | IllegalArgumentException | NullPointerException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }

        Page<Object[]> repoRes = subscriptionRepository
            .findAddNeededSubscriptionsInfoByUserInfoId(request.getId(), subscriptionInfo, pageableCfg);
        return TablePagesResponse.<SubscriptionsInfoResponse>builder()
            .data(repoRes.stream().map(SubscriptionsInfoResponse::buildFromNativeQuery).toList())
            .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
    }

    public SessionToPerformResponse getSessionsOfSubscribedScheduleOfUser(ScheduleInfoToPerformSessionRequest request,
                                                                          String accessToken) {
        return subscriptionRepository.getSessionsOfSubscribedScheduleByIdAndEmail(
            jwtService.readPayload(accessToken).get("sub"), request)
            .orElseThrow(() -> new ApplicationException(ErrorCodes.NOT_SUBSCRIBED_SESSION_YET));
    }

    public List<ExercisesOfSessions> getExercisesInSessionOfSubscribedScheduleOfUser(ByIdDto request,
                                                                                     String accessToken) {
        var result = subscriptionRepository.getExercisesInSessionOfSubscribedScheduleByIdAndEmail(
            jwtService.readPayload(accessToken).get("sub"), request.getId());
        if (result.isEmpty())
            throw new ApplicationException(ErrorCodes.NOT_SUBSCRIBED_EXERCISES_YET);
        return result;
    }

    public PreviewScheduleResponse getPreviewScheduleInfoForUserToSubscribe(ByIdDto request, String accessToken) {
        Schedule schedule = scheduleRepository.findById(request.getId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        return PreviewScheduleResponse.builder()
            .schedule(schedule)
            .totalSessions(schedule.getSessionsOfSchedule().size())
            .sessionsOfSchedules(schedule.getSessionsOfSchedule().stream().map(session ->
                PreviewScheduleResponse.PreviewSession.builder()
                    .session(session)
                    .exerciseNames(session.getExercisesOfSession().stream().map(Exercise::getName)
                        .collect(Collectors.toSet()))
                    .build()
            ).collect(Collectors.toSet()))
            .wasSubscribed(subscriptionRepository
                .existsByScheduleScheduleIdAndChangingCoinsHistoriesUserInfoUserEmail(request.getId(),
                jwtService.readPayload(accessToken).get("sub")))
            .build();
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public void subscribeSchedule(ScheduleSubscriptionRequest request, String accessToken) {
        var userInfo = this.getUserInfoToUpdateCoinsWithLock(null, jwtService.readPayload(accessToken).get("sub"));
        var subscribedSchedule = scheduleRepository.findById(request.getScheduleId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));

        //--Subtract Coins.
        final long subtractedCoins = userInfo.getCoins() - subscribedSchedule.getCoins();
        if (subtractedCoins < 0)
            throw new ApplicationException(ErrorCodes.NOT_ENOUGH_COINS);
        userInfo.setCoins(userInfo.getCoins() - subscribedSchedule.getCoins());

        var usedCoinsHistory = changingCoinsHistoriesRepository.save(ChangingCoinsHistories.builder()
            .changingCoinsHistoriesId(UUID.randomUUID().toString().substring(0, 8))
            .userInfo(userInfo)
            .changingCoins(subscribedSchedule.getCoins())
            .description("Used coins for subscription")
            .changingTime(LocalDateTime.now())
            .changingCoinsType(ChangingCoinsType.USING)
            .build());

        subscriptionRepository.save(Subscription.builder()
            .schedule(subscribedSchedule)
            .changingCoinsHistories(usedCoinsHistory)
            .aim(Aim.getByType(request.getAimType()))
            .efficientDays(null)
            .bmr(null)
            .repRatio(request.getRepRatio())
            .weightAim(null)
            .completedTime(null)
            .build());
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public void subscribeScheduleWithAI(ScheduleSubscriptionWithAIRequest request, String accessToken) {
        var userInfo = this.getUserInfoToUpdateCoinsWithLock(null, jwtService.readPayload(accessToken).get("sub"));
        var subscribedSchedule = scheduleRepository.findById(request.getScheduleId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));

        //--Subtract Coins.
        final long subtractedCoins = userInfo.getCoins() - subscribedSchedule.getCoins();
        if (subtractedCoins < 0)
            throw new ApplicationException(ErrorCodes.NOT_ENOUGH_COINS);
        userInfo.setCoins(userInfo.getCoins() - subscribedSchedule.getCoins());

        var usedCoinsHistory = changingCoinsHistoriesRepository.save(ChangingCoinsHistories.builder()
            .changingCoinsHistoriesId(UUID.randomUUID().toString().substring(0, 8))
            .userInfo(userInfo)
            .changingCoins(subscribedSchedule.getCoins())
            .description("Used coins for subscription with AI")
            .changingTime(LocalDateTime.now())
            .changingCoinsType(ChangingCoinsType.USING)
            .build());

        var subscription = Subscription.builder()
            .schedule(subscribedSchedule)
            .changingCoinsHistories(usedCoinsHistory)
            .aim(Aim.getByType(request.getAimType()))
            .repRatio(request.getRepRatio())
            .completedTime(null)
            .build();

        this.calculateBMR(subscription, request);
        subscription.setWeightAim(Objects.isNull(request.getWeightAimByDiet())? request.getWeight()
            : request.getWeightAimByDiet());
        subscriptionRepository.save(subscription);
    }

    public UserInfo getUserInfoToUpdateCoinsWithLock(Long id, String email) throws ApplicationException {
        for (int times = 1; times <= 3; times++) {
            try {
                return userInfoRepository.findByIdOrEmailWithLock(id, email)
                    .orElseThrow(() -> new ApplicationException(ErrorCodes.FORBIDDEN_USER));
            } catch (PessimisticLockException e) {
                log.warn("Pessimistic Lock found a contention when Updating Coins (for Subscribe Schedule)");
                continue;
            }
        }
        throw new ApplicationException(ErrorCodes.TRANSACTION_VIOLATION_FROM_SUBSCRIPTION);
    }

    private void calculateBMR(Subscription updatedSubscription, ScheduleSubscriptionWithAIRequest request) {
        Map<String, Integer> bodyInfo = SubscriptionService.calculateTDEE(
            request.getWeight(),
            request.getBodyFat(),
            updatedSubscription.getSchedule().getSessionsOfSchedule().size()
        );
        updatedSubscription.setBmr(Double.valueOf(bodyInfo.get("BMR")));
        if (Objects.isNull(request.getAimRatio()))
            return; //--Maintain Weight: stop right here, no more provided info.
        if (Objects.isNull(request.getWeightAimByDiet())) {
            updatedSubscription.setEfficientDays(null);
            return; //--Raise Weight: stop right here.
        }

        final double consumedCaloPerDay = (double) (bodyInfo.get("TDEE") * (100 + request.getAimRatio())) / 100;
        final double lostWeight = request.getWeight() - request.getWeightAimByDiet();
        final int efficientDays = (int) (7700 * lostWeight / consumedCaloPerDay);   //--7700calo / 1kg fat
        updatedSubscription.setEfficientDays(efficientDays);
    }

    public static Map<String, Integer> calculateTDEE(Float weight, Long bodyFat, int totalSessions) {
        final double LBM = weight * (1 - (double) bodyFat /100);
        final double BMR = Math.floor(370 + (21.6 * LBM));
        final double R = switch(totalSessions) {
            case 1, 2, 3 -> 1.375;
            case 4, 5 -> 1.55;
            case 6, 7 -> 1.725;
            default -> 0;
        };
        final double TDEE = Math.floor(BMR * R);
        return Map.of("BMR", (int) BMR, "TDEE", (int) TDEE);
    }

    public static double calculateTDEE(double BMR, int totalSessions) {
        final double R = switch(totalSessions) {
            case 1, 2, 3 -> 1.375;
            case 4, 5 -> 1.55;
            case 6, 7 -> 1.725;
            default -> 0;
        };
        return BMR * R;
    }
}
