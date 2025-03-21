package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DataFactoryController {
    
    @GetMapping("/data/reset")
    public String resetData(Model model) {
        return "data/reset";
    }
}
