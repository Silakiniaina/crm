package site.easy.to.build.crm.service.expense;

import java.util.List;

import site.easy.to.build.crm.entity.Expense;

public interface ExpenseService {
    
    public Expense addExpense(Expense expense) throws Exception;

    public List<Expense> findExpensesByFilters(Integer type, Integer id);

    public double getTotalExpenseByCustomerId(int customerId);

    public void validateBudgetOverrun(Expense expense) throws Exception;
}
