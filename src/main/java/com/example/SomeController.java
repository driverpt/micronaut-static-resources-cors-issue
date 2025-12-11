package com.example;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/some/controller")
public class SomeController {

    @Get
    public String hello() {
        return "Hello, World!";
    }
}
