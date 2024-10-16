package com.restproject.backend.dtos.general;

import java.lang.reflect.Field;

public class ObjectDto {

    public static void mappingValues(Object result, Object[] params) {
        var fields = result.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length && i < params.length; i++) {
            try {
                Field field = fields[i];
                field.setAccessible(true);
                field.set(result, params[i]); // Set the field value in the response object
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error setting field value", e);
            }
        }
    }
}
