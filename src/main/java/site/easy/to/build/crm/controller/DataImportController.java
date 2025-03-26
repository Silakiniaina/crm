package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.service.data.DataImportService;
import site.easy.to.build.crm.exception.DataImportException;

import java.util.List;
import java.util.Map;

@Controller
public class DataImportController {

    private final DataImportService dataImportService;

    @Autowired
    public DataImportController(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    @GetMapping("/data-import")
    public String showImportPage() {
        return "data/data-import";
    }

    @PostMapping("/data-import")
    public String importData(
            @RequestParam(value = "customerFile", required = false) MultipartFile customerFile,
            @RequestParam(value = "budgetFile", required = false) MultipartFile budgetFile,
            @RequestParam(value = "expenseFile", required = false) MultipartFile expenseFile,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Map<String, List<String>> errors = dataImportService.importData(
                customerFile,
                budgetFile,
                expenseFile,
                authentication
            );

            if (errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Data imported successfully");
            } else {
                redirectAttributes.addFlashAttribute("errorMap", errors);
            }
        } catch (DataImportException e) {
            redirectAttributes.addFlashAttribute("errorMap", e.getError());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMap", 
                Map.of("Import Error", List.of("Unexpected error during import: " + e.getMessage())));
        }
        
        return "redirect:/data-import";
    }
}