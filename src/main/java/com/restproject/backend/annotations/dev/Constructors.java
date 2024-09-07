package com.restproject.backend.annotations.dev;

import java.lang.annotation.*;

/**
 * README: This annotation is used to make clarify method's meaning of Constructors.
 */
@Target({ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Constructors {
}
