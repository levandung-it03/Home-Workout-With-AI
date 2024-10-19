package com.restproject.backend.services;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.request.ScheduleByStatusRequest;
import com.restproject.backend.dtos.request.SubscriptionsInfoRequest;
import com.restproject.backend.dtos.response.SubscriptionsInfoResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.Session;
import com.restproject.backend.entities.Subscription;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.repositories.SubscriptionRepository;
import com.restproject.backend.services.Auth.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionService {
    SubscriptionRepository subscriptionRepository;
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

    public Session getSessionsOfSubscribedScheduleOfUser(ByIdDto request, String accessToken) {
        return subscriptionRepository.getSessionsOfSubscribedScheduleByIdAndEmail(
            jwtService.readPayload(accessToken).get("sub"),
            request.getId()
        ).orElseThrow(() -> new ApplicationException(ErrorCodes.NOT_SUBSCRIBED_SESSION_YET));
    }

    public List<Exercise> getExercisesInSessionOfSubscribedScheduleOfUser(ByIdDto request, String accessToken) {
        List<Exercise> result = subscriptionRepository.getExercisesInSessionOfSubscribedScheduleByIdAndEmail(
            jwtService.readPayload(accessToken).get("sub"), request.getId());
        if (result.isEmpty())
            throw new ApplicationException(ErrorCodes.NOT_SUBSCRIBED_EXERCISES_YET);
        return result;
    }
}
