package ru.otus.hw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error")
    public String viewErrorPage() {
        return "errors/error";
    }

    @GetMapping("/custom_error")
    public String viewCustomErrorPage() {
        return "errors/customError";
    }

    @GetMapping("/error/403")
    public String viewError403Page() {
        return "errors/error_403";
    }
}
