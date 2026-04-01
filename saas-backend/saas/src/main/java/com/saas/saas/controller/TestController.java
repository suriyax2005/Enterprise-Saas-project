package com.saas.saas.controller;

import com.saas.saas.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final EmailService emailService;

    public TestController(
            EmailService emailService
    ){
        this.emailService = emailService;
    }

    @GetMapping("/test-email")
    public String testEmail(){

        emailService.sendEmail(

                "suriya0ajju@gmail.com",

                "Spring Boot Test",

                "Email working successfully"

        );

        return "Email sent";
    }
}