package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import site.easy.to.build.crm.service.data.DataFactoryService;

@Controller
public class DataFactoryController {
    
    private final DataFactoryService dataFactoryService;

    @Autowired
    public DataFactoryController(DataFactoryService dataFactoryService) {
        this.dataFactoryService = dataFactoryService;
    }

    @GetMapping("/data/reset")
    public String resetData(Model model) {
        return "data/reset";
    }

    @PostMapping("/data/reset")
    public String resetDataPost(Model model) {
        dataFactoryService.resetData();
        return "data/reset";
    }
}
