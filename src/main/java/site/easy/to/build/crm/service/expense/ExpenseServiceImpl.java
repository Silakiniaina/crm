package site.easy.to.build.crm.service.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    @Override
    public List<Expense> findExpensesByFilters(Integer type, Integer id) {
        if (type != null && id != null) {
            // Both filters are provided
            return expenseRepository.findByExpenseTypeAndRelatedId(type, id);
        } else if (type != null) {
            // Only type filter is provided
            return expenseRepository.findByExpenseType(type);
        } else if (id != null) {
            // Only id filter is provided
            return expenseRepository.findByRelatedId(id);
        } else {
            // No filters, return all expenses
            return expenseRepository.findAll();
        }
    }

    @Override
    public double getTotalExpenseByCustomerId(int customerId) {
        BigDecimal total = expenseRepository.findTotalExpenseByCustomerId(customerId);
        return total != null ? total.doubleValue() : 0.0;
    }
}
