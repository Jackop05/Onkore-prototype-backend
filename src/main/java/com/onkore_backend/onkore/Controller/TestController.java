package com.onkore_backend.onkore.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String renderEmptyPage() {
        return "Nothing to be seen here";
    }

    @GetMapping("/testing")
    public String renderPlainText() {
        return "Everything working just fine";
    }
}
