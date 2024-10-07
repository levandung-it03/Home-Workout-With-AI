package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.SessionHasMusclesResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.MusclesOfSessionsService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MusclesOfSessionsControllers {
    MusclesOfSessionsService musclesOfSessionsServiceOfAdmin;

    @ResponseBody
    @GetMapping("/admin/v1/get-sessions-has-muscles-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<SessionHasMusclesResponse>>> getSessionsHasMusclesPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSIONS_HAS_MUSCLES_PAGES,
            musclesOfSessionsServiceOfAdmin.getSessionsHasMusclesPages(request));
    }

}
