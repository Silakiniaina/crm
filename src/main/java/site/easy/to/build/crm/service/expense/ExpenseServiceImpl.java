package site.easy.to.build.crm.service.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.repository.ExpenseRepository;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Expense addExpense(Expense expense) throws Exception {
        if (expense == null) {
            throw new Exception("Expense cannot be null");
        }
        try {
            return expenseRepository.save(expense);
        } catch (Exception e) {
            throw new Exception("Failed to save expense: " + e.getMessage());
        }
    }
}
