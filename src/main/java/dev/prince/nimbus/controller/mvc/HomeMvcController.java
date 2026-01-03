package dev.prince.nimbus.controller.mvc;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Tag(name = "HomeMvcController")
public class HomeMvcController {

    @GetMapping("/api/v1/mvc/home")
    public String home(Model model) {
        model.addAttribute("appName", "Nimbus");
        return "home";
    }
}
