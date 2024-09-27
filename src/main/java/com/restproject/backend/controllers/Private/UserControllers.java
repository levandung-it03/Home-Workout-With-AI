package com.restproject.backend.controllers.Private;

import com.restproject.backend.dtos.request.UpdateUserStatusRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Admin.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserControllers {
    UserService userService;

    @ResponseBody
    @PostMapping("/admin/v1/update-user-status")
    public ResponseEntity<ApiResponseObject<Void>> updateUserStatus(
        @Valid @RequestBody UpdateUserStatusRequest request) {
        userService.updateUserStatus(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_USER_STATUS);
    }
}
