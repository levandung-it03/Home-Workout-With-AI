package com.restproject.backend.controllers.Test;

import com.restproject.backend.dtos.general.TestDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestController {

    @ResponseBody
    @PostMapping(value = "/api/private/auth/v1/free")
    public ResponseEntity<String> free(
        @Valid @RequestBody TestDto body
    ) {
        System.out.println(body);
        return ResponseEntity.ok("hello");
    }

    @ResponseBody
    @GetMapping(value = "/api/private/auth/get-free")
    public ResponseEntity<String> getFree() {
        return ResponseEntity.ok("hello");
    }
}
