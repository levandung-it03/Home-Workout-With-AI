package com.restproject.backend.controllers;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.general.ImageDto;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.entities.Slides;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.SlidesService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlidesControllers {
    SlidesService slidesService;

    @ResponseBody
    @GetMapping({"/api/private/admin/v1/get-all-slides-for-home", "/api/private/user/v1/get-all-slides-for-home"})
    public ResponseEntity<ApiResponseObject<List<Slides>>> getAllSlidesForHome() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_SLIDES_FOR_HOME,
            slidesService.getAllSlidesForHome());
    }

    @ResponseBody
    @DeleteMapping("/api/private/admin/v1/delete-slide")
    public ResponseEntity<ApiResponseObject<Void>> getAllSlidesForHome(@Valid @RequestBody ByIdDto request)
        throws IOException {
        slidesService.deleteSlide(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPLOAD_SLIDES);
    }

    @ResponseBody
    @PostMapping(value = "/api/private/admin/v1/upload-slide",
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponseObject<Slides>> getAllSlidesForHome(@Valid ImageDto request)
        throws IOException {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPLOAD_SLIDES,
            slidesService.uploadSlide(request));
    }
}
