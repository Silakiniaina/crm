package site.easy.to.build.crm.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.service.data.ImportDataServiceImpl;

@Controller
public class DataImportController {

    private final ImportDataServiceImpl importDataService;
    private final Map<String, Class<?>> tableEntityMap;
    
    @GetMapping("/data/import")
    public String importData(Model model) {
        return "data/import";
    }

    
    @Autowired
    public DataImportController(ImportDataServiceImpl importDataService) {
        this.importDataService = importDataService;
        this.tableEntityMap = new HashMap<>();
        tableEntityMap.put("roles", site.easy.to.build.crm.entity.Role.class);
    }
    
    @PostMapping("/data/import")
    public String importData(
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam("tableName") String tableName,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (multipartFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload");
            return "redirect:/data/import";
        }
        
        if (!tableEntityMap.containsKey(tableName)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid table selection");
            return "redirect:/data/import";
        }
        File tempFile = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String fileExtension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".tmp";
            tempFile = File.createTempFile(UUID.randomUUID().toString(), fileExtension);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(multipartFile.getBytes());
            }
            Class<?> entityClass = tableEntityMap.get(tableName);
            importDataService.importData(tempFile, entityClass);

            // redirectAttributes.addFlashAttribute("successMessage", 
            //         importedCount + " records successfully imported to " + tableName + " table");
            
            return "redirect:/data/import";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to process the file: " + e.getMessage());
            return "redirect:/data/import";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error importing data: " + e.getMessage());
            return "redirect:/data/import";
        } finally {
            if (tempFile != null && tempFile.exists()) {
                try {
                    Files.delete(tempFile.toPath());
                } catch (IOException e) {
                    System.err.println("Failed to delete temporary file: " + e.getMessage());
                }
            }
        }
    }
}
