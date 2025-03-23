package site.easy.to.build.crm.service.budget;

import site.easy.to.build.crm.entity.Expense;

public interface BudgetAlertThresholdService {
    
    public double getThreshold();

    public boolean isExpenseExceedThreshold(Expense expense);

}
