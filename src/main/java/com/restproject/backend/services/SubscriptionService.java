package com.restproject.backend.services;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.PreviewScheduleResponse;
import com.restproject.backend.dtos.response.SessionToPerformResponse;
import com.restproject.backend.dtos.response.SubscriptionsInfoResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.*;
import com.restproject.backend.enums.Aim;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.repositories.ScheduleRepository;
import com.restproject.backend.repositories.SubscriptionRepository;
import com.restproject.backend.repositories.UserInfoRepository;
import com.restproject.backend.services.Auth.JwtService;
import jakarta.persistence.OptimisticLockException;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionService {
    SubscriptionRepository subscriptionRepository;
    UserInfoRepository userInfoRepository;
    ScheduleRepository scheduleRepository;
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
        Schedule schedule = subscriptionRepository
            .findScheduleToSubscribe(jwtService.readPayload(accessToken).get("sub"), request.getId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.WAS_SUBSCRIBED_SCHEDULE));
        return PreviewScheduleResponse.builder()
            .schedule(schedule)
            .totalSessions(schedule.getSessionsOfSchedule().size())
            .sessionsOfSchedules(schedule.getSessionsOfSchedule().stream().map(session ->
                PreviewScheduleResponse.PreviewSession.builder()
                    .session(session)
                    .exerciseNames(session.getExercisesOfSession().stream().map(Exercise::getName)
                        .collect(Collectors.toSet()))
                    .build()
            ).collect(Collectors.toSet())).build();
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public void subscribeSchedule(ScheduleSubscriptionRequest request, String accessToken) {
        var userInfo = userInfoRepository.findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.FORBIDDEN_USER));
        var subscribedSchedule = scheduleRepository.findById(request.getScheduleId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));

        //--Subtract Coins.
        final long subtractedCoins = userInfo.getCoins() - subscribedSchedule.getCoins();
        if (subtractedCoins < 0)
            throw new ApplicationException(ErrorCodes.NOT_ENOUGH_COINS);
        this.subtractCoinsWhenSubscribeSchedule(subtractedCoins, userInfo.getUserInfoId());

        subscriptionRepository.save(Subscription.builder()
            .schedule(subscribedSchedule)
            .userInfo(userInfo)
            .aim(Aim.getByType(request.getAimType()))
            .efficientDays(null)
            .bmr(null)
            .repRatio(request.getRepRatio())
            .subscribedTime(LocalDateTime.now())
            .completedTime(null)
            .build());
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public void subscribeScheduleWithAI(ScheduleSubscriptionWithAIRequest request, String accessToken) {
        var userInfo = userInfoRepository.findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.FORBIDDEN_USER));
        var subscribedSchedule = scheduleRepository.findById(request.getScheduleId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));

        //--Subtract Coins.
        final long subtractedCoins = userInfo.getCoins() - subscribedSchedule.getCoins();
        if (subtractedCoins < 0)
            throw new ApplicationException(ErrorCodes.NOT_ENOUGH_COINS);
        this.subtractCoinsWhenSubscribeSchedule(subtractedCoins, userInfo.getUserInfoId());

        var subscription = Subscription.builder()
            .schedule(subscribedSchedule)
            .userInfo(userInfo)
            .aim(Aim.getByType(request.getAimType()))
            .repRatio(request.getRepRatio())
            .subscribedTime(LocalDateTime.now())
            .completedTime(null)
            .build();

        this.calculateBMR(subscription, request);
        subscriptionRepository.save(subscription);
    }

    private void subtractCoinsWhenSubscribeSchedule(long newCoins, long userInfoId) throws ApplicationException {
        int retries = 3;
        while (retries > 0) {
            try {
                userInfoRepository.updateCoins(newCoins, userInfoId);
                return;
            } catch (OptimisticLockException e) {
                log.warn("Pessimistic Lock found a contention when Updating Coins (for Subscribe Schedule)");
                if (--retries == 0)
                    throw new ApplicationException(ErrorCodes.TRANSACTION_VIOLATION_FROM_SUBSCRIPTION);
            }
        }
    }

    private void calculateBMR(Subscription updatedSubscription, ScheduleSubscriptionWithAIRequest request) {
        final double LBM = request.getHeight() * (100 - request.getBodyFat()) / 100;
        final double BMR = 370 + (21.6 * LBM);
        updatedSubscription.setBmr(BMR);
        if (Objects.isNull(request.getAimRatio()))
            return; //--Maintain Weight: stop right here, no more provided info.
        if (Objects.isNull(request.getWeightAimByDiet())) {
            updatedSubscription.setEfficientDays(null);
            return; //--Raise Weight: stop right here.
        }

        final int totalSessionsAWeek = updatedSubscription.getSchedule().getSessionsOfSchedule().size();
        final double R = switch(totalSessionsAWeek) {
            case 1, 2, 3 -> 1.375;
            case 4, 5 -> 1.55;
            case 6, 7 -> 1.725;
            default -> 0;
        };
        final double TDEE = BMR * R;
        final double consumedCaloPerDay = TDEE * (100 + request.getAimRatio()) / 100;
        final double lostWeight = request.getWeight() - request.getWeightAimByDiet();
        final int efficientDays = (int) (7700 * lostWeight / consumedCaloPerDay);   //--7700calo / 1kg fat
        updatedSubscription.setEfficientDays(efficientDays);
    }
}
