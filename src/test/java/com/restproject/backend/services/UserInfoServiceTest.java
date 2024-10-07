package com.restproject.backend.services;

import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoServiceTest {
    @Autowired
    UserInfoService userInfoService;


    @Test
    public void getUserInfoAndStatusPages_admin_invalidSortedField() {
        var ftr = new HashMap<String, Object>();
        ftr.put("firstName", "Stre");
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).sortedField("unknown").build();
        var exc = assertThrows(ApplicationException.class, () -> userInfoService
            .getUserInfoAndStatusPages(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_SORTING_FIELD_OR_VALUE);
    }


    @Test
    public void getUserInfoAndStatusPages_admin_invalidFilteringValues() {
        var ftr = new HashMap<String, Object>();
        ftr.put("genderId", 2);
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).build();

        var exc = assertThrows(ApplicationException.class, () -> userInfoService
            .getUserInfoAndStatusPages(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
    }

    @Test
    public void getUserInfoAndStatusPages_admin_invalidFilteringFields() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).build();

        var exc = assertThrows(ApplicationException.class, () -> userInfoService
            .getUserInfoAndStatusPages(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
    }
}
