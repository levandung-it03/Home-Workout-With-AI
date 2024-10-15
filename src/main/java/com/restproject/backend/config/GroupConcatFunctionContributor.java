package com.restproject.backend.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Component;

@Component
public class GroupConcatFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();

        // Registering GROUP_CONCAT function with a return type of String
        functionRegistry.register("GROUP_CONCAT",
            new StandardSQLFunction("GROUP_CONCAT", StandardBasicTypes.STRING)
        );
    }
}
