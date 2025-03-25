package site.easy.to.build.crm.service.budget;

import site.easy.to.build.crm.entity.BudgetAlertThreshold;
import site.easy.to.build.crm.entity.Expense;

public interface BudgetAlertThresholdService {
    
    public double getThreshold();

    public void save(BudgetAlertThreshold threshold) ;

    public boolean isExpenseExceedThreshold(Expense expense);

    public BudgetAlertThreshold getThresholdObject();

}
