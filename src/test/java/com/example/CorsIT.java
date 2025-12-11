/*
 * Copyright (c) 2025 Ryanair Ltd. All rights reserved.
 */
package com.example;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.http.server.cors.CorsOriginConfiguration;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CorsIT {

    @Inject
    HttpServerConfiguration.CorsConfiguration corsConfiguration;

    Stream<Arguments> corsOrigins() {
        CorsOriginConfiguration defaultConfig = corsConfiguration.getConfigurations().get("default");

        return defaultConfig.getAllowedOrigins().stream()
                .map(Arguments::of);
    }

    @ParameterizedTest(name = "CORS requests are handled properly for origin: {0}")
    @MethodSource("corsOrigins")
    void corsHandledProperly(String origin,
                             RequestSpecification spec) {
        given(spec)
                .when()
                .header(HttpHeaders.ORIGIN, origin)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name())
                .options("/swagger/swagger.yml")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.GET.name());
    }

    @Test
    void corsUnknownOriginsAreProperlyRejected(RequestSpecification spec) {
        given(spec)
                .when()
                .header(HttpHeaders.ORIGIN, "https://some.random.origin")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name())
                .options("/swagger/swagger.yml")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.getCode());
    }
}
