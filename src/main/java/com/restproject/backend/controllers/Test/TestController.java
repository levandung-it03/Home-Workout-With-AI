package com.restproject.backend.controllers.Test;

import com.restproject.backend.dtos.general.TestDto;
import com.restproject.backend.dtos.request.SEPayWebHookRequest;
import com.restproject.backend.services.ThirdParty.SEPayService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestController {
    SEPayService sePayService;

    @ResponseBody
    @PostMapping(value = "/free")
    public ResponseEntity<String> free(@Valid @RequestBody TestDto body) {
        System.out.println(body);
        return ResponseEntity.ok("hello");
    }

    @ResponseBody
    @GetMapping(value = "/get-free")
    public ResponseEntity<String> getFree() {
        return ResponseEntity.ok("hello");
    }

    @ResponseBody
    @PostMapping(value = "/sepay/webhook")
    public ResponseEntity<String> webhook(@RequestBody SEPayWebHookRequest request) {
        sePayService.checkDescriptionAndAccumulateCoins(request);
        return ResponseEntity.ok("Money Received.");
    }
}
