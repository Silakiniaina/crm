package site.easy.to.build.crm.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.util.Response;
import site.easy.to.build.crm.util.ResponseUtil;
import site.easy.to.build.crm.dto.TotalDataDTO;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.service.dashboard.TotalDataService;
import site.easy.to.build.crm.service.expense.ExpenseServiceImpl;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final TotalDataService totalDataService;
    private final ExpenseServiceImpl expenseService;

    @Autowired
    public ApiController(ExpenseServiceImpl exp,TotalDataService totalDataService) {
        this.totalDataService = totalDataService;
        this.expenseService = exp;
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

    @GetMapping("/expenses/{id}")
    public <T> ResponseEntity<Response<T>> getExpenseById(@PathVariable("id") int id) {
        try {
            Expense expense = expenseService.findById(id);
            if (expense == null) {
                throw new IllegalArgumentException("Expense not found with ID: " + id);
            }
            return ResponseUtil.sendResponse(HttpStatus.OK, true, "Expense retrieved successfully", (T) expense);
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.BAD_REQUEST, false, "Error while fetching expense", (T) e.getMessage());
        }
    }

    @PostMapping("/expenses/update")
    public <T> ResponseEntity<Response<T>> updateExpenseAmount(
            @RequestParam("id") int id,
            @RequestParam("amount") BigDecimal amount) {
        try {
            Expense expense = expenseService.findById(id);
            if (expense == null) {
                throw new IllegalArgumentException("Expense not found with ID: " + id);
            }
            expense.setAmount(amount);
            expenseService.addExpense(expense,false);
            return ResponseUtil.sendResponse(HttpStatus.OK, true, "Expense amount updated successfully", (T) expense);
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.BAD_REQUEST, false, "Error while updating expense", (T) e.getMessage());
        }
    }
}
