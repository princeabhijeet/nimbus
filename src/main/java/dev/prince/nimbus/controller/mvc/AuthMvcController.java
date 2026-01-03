package dev.prince.nimbus.controller.mvc;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Tag(name = "AuthMvcController")
public class AuthMvcController {

    @GetMapping("/api/v1/mvc/register")
    public String register() {
        return "register";
    }

    @GetMapping("/api/v1/mvc/login/github")
    public String loginWithGithub() {
        return "login-github";
    }

    @GetMapping("/api/v1/mvc/user/details")
    public String userDetails() {
        return "user-details";
    }

}
