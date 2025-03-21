package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DataImportController {
    
    @GetMapping("/data/import")
    public String importData(Model model) {
        return "data/import";
    }
}
