package com.restproject.backend.annotations.dev;

import java.lang.annotation.*;

/**
 * README: This annotation is used to make clarify method's meaning because it's used to be re-used by the others.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CoreEngines {
}
