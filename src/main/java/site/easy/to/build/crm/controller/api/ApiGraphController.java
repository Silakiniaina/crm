package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.easy.to.build.crm.service.graph.GraphDataService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/graphs")
public class ApiGraphController {

    private final GraphDataService graphDataService;

    @Autowired
    public ApiGraphController(GraphDataService graphDataService) {
        this.graphDataService = graphDataService;
    }

    @GetMapping("/budget-vs-expenses")
    public ResponseEntity<List<Map<String, Object>>> getBudgetVsExpensesByCustomer(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<Map<String, Object>> data = graphDataService.getBudgetVsExpensesByCustomer(limit);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/lead-conversion-rate")
    public ResponseEntity<List<Map<String, Object>>> getLeadConversionRateByMonth() {
        List<Map<String, Object>> data = graphDataService.getLeadConversionRateByMonth();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/avg-expenses")
    public ResponseEntity<List<Map<String, Object>>> getAverageExpensesByType() {
        List<Map<String, Object>> data = graphDataService.getAverageExpensesByType();
        return ResponseEntity.ok(data);
    }
}