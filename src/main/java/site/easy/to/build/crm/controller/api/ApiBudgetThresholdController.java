package site.easy.to.build.crm.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.entity.BudgetAlertThreshold;
import site.easy.to.build.crm.service.budget.BudgetAlertThresholdServiceImpl;
import site.easy.to.build.crm.util.Response;
import site.easy.to.build.crm.util.ResponseUtil;

@RestController
@RequestMapping("/api/budget-thresholds")
public class ApiBudgetThresholdController {

    private final BudgetAlertThresholdServiceImpl thresholdService;

    public ApiBudgetThresholdController(BudgetAlertThresholdServiceImpl thresholdService) {
        this.thresholdService = thresholdService;
    }

    @SuppressWarnings("unchecked")
    @GetMapping
    public <T> ResponseEntity<Response<T>> getThreshold() {
        try {
            BudgetAlertThreshold threshold = thresholdService.getThresholdObject();
            if (threshold == null) {
                return ResponseUtil.sendResponse(HttpStatus.NOT_FOUND, false, "No threshold defined", null);
            }
            return ResponseUtil.sendResponse(HttpStatus.OK, true, "Threshold retrieved successfully", (T)threshold);
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, false, "Error while fetching threshold", (T)e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @PutMapping
    public <T> ResponseEntity<Response<T>> updateThreshold(
            @RequestBody BudgetAlertThreshold thresholdRequest) {
        try {
            BudgetAlertThreshold existingThreshold = thresholdService.getThresholdObject();
            
            if (existingThreshold == null) {
                existingThreshold = new BudgetAlertThreshold();
            }
            
            existingThreshold.setThreshold(thresholdRequest.getThreshold());
            thresholdService.save(existingThreshold);
            
            return ResponseUtil.sendResponse(HttpStatus.OK, true, "Threshold updated successfully", (T)existingThreshold);
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, false, "Error while updating threshold", (T)e.getMessage());
        }
    }
}