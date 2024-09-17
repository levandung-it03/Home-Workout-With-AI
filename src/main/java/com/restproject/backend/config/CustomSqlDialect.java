package com.restproject.backend.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.spi.TypeConfiguration;

public class CustomSqlDialect extends MySQLDialect {
    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);
        SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();
        TypeConfiguration typeConfiguration = functionContributions.getTypeConfiguration();

        functionRegistry.namedDescriptorBuilder("GROUP_CONCAT")
            .setInvariantType(typeConfiguration.getBasicTypeForJavaType(String.class))
            .setMinArgumentCount(1)
            .setArgumentListSignature("(?)")
            .register();
    }
}
