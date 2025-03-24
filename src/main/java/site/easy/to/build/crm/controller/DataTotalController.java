package site.easy.to.build.crm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.util.Response;
import site.easy.to.build.crm.util.ResponseUtil;
import site.easy.to.build.crm.dto.TotalDataDTO;
import site.easy.to.build.crm.service.dashboard.TotalDataService;

@RestController
@RequestMapping("/api")
public class DataTotalController {

    private final TotalDataService totalDataService;

    @Autowired
    public DataTotalController(TotalDataService totalDataService) {
        this.totalDataService = totalDataService;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/data-total")
    public <T> ResponseEntity<Response<T>> getTotalData() {
        try {
            TotalDataDTO totalData = totalDataService.getTotalData();
            return ResponseUtil.sendResponse(HttpStatus.OK, true, "Total data retrieved successfully", (T) totalData);
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.BAD_REQUEST, false, "Error while fetching total data", (T) e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/data-total/details")
    public <T> ResponseEntity<Response<T>> getTotalDataDetails(@RequestParam("type") int type) {
        try {
            List<?> details = totalDataService.getTotalDataDetails(type);
            return ResponseUtil.sendResponse(HttpStatus.OK, true, "Details retrieved successfully", (T) details);
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.BAD_REQUEST, false, "Error while fetching details", (T) e.getMessage());
        }
    }
}
