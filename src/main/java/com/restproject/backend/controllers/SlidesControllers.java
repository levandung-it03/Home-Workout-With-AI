package com.restproject.backend.controllers;

import com.restproject.backend.dtos.general.ImageDto;
import com.restproject.backend.dtos.request.DeleteObjectRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.entities.Slides;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.SlidesService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/private")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "Bearer", bearerFormat = "JWT")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlidesControllers {
    SlidesService slidesService;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get all slide's images to show up")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=29001; message=Get all Slides successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @ResponseBody
    @GetMapping({"/admin/v1/get-all-slides-for-home", "/user/v1/get-all-slides-for-home"})
    public ResponseEntity<ApiResponseObject<List<Slides>>> getAllSlidesForHome() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_SLIDES_FOR_HOME,
            slidesService.getAllSlidesForHome());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete specified image slide")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=29003; message=Delete Slide successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10005; message=Can not get object because id or primary fields are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @ResponseBody
    @DeleteMapping("/admin/v1/delete-slide")
    public ResponseEntity<ApiResponseObject<Void>> deleteSlide(@Valid @RequestBody DeleteObjectRequest request)
        throws IOException {
        slidesService.deleteSlide(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DELETE_SLIDES);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload new image slide")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=29002; message=Upload Slide successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @ResponseBody
    @PostMapping(value = "/admin/v1/upload-slide",
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponseObject<Slides>> uploadSlide(@Valid ImageDto request) throws IOException {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPLOAD_SLIDES, slidesService.uploadSlide(request));
    }
}
