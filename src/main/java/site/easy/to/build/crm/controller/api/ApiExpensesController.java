package site.easy.to.build.crm.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.service.expense.ExpenseServiceImpl;
import site.easy.to.build.crm.util.Response;
import site.easy.to.build.crm.util.ResponseUtil;

@RestController
@RequestMapping("/api/expenses")
public class ApiExpensesController {

    private final ExpenseServiceImpl expenseService;

    public ApiExpensesController(ExpenseServiceImpl expenseService) {
        this.expenseService = expenseService;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/{id}")
    public <T> ResponseEntity<Response<T>> getExpenseById(@PathVariable("id") int id) {
        try {
            Expense expense = expenseService.findById(id);
            if (expense == null) {
                return ResponseUtil.sendResponse(HttpStatus.NOT_FOUND, false, "Expense not found", null);
            }
            return ResponseUtil.sendResponse(HttpStatus.OK, true, "Expense retrieved successfully", (T)expense);
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, false, "Error while fetching expense", (T)e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @PutMapping("/{id}")
    public <T> ResponseEntity<Response<T>> updateExpense(
            @PathVariable("id") int id,
            @RequestBody Expense updatedExpense) {
        try {
            Expense existingExpense = expenseService.findById(id);
            if (existingExpense == null) {
                return ResponseUtil.sendResponse(HttpStatus.NOT_FOUND, false, "Expense not found", null);
            }
            
            existingExpense.setAmount(updatedExpense.getAmount());
            expenseService.addExpense(existingExpense, false);
            
            return ResponseUtil.sendResponse(HttpStatus.OK, true, "Expense updated successfully", (T)existingExpense);
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, false, "Error while updating expense", (T)e.getMessage());
        }
    }
}