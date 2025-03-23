package site.easy.to.build.crm.service.budget;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.BudgetAlertThreshold;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerBudget;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.repository.BudgetAlertThresholdRepository;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;
import site.easy.to.build.crm.service.expense.ExpenseServiceImpl;

@Service
public class BudgetAlertThresholdServiceImpl implements BudgetAlertThresholdService {

    private final BudgetAlertThresholdRepository budgetAlertThresholdRepository;
    private final ExpenseServiceImpl expenseService;
    private final CustomerServiceImpl customerService;

    @Autowired
    public BudgetAlertThresholdServiceImpl(CustomerServiceImpl cust,ExpenseServiceImpl expense,BudgetAlertThresholdRepository budgetAlertThresholdRepository) {
        this.budgetAlertThresholdRepository = budgetAlertThresholdRepository;
        this.expenseService = expense;
        this.customerService = cust;
    }
    
    @Override
    public double getThreshold() {
        try {
            Optional<BudgetAlertThreshold> thresholdOpt = budgetAlertThresholdRepository.findById(1);
            
            return thresholdOpt
                .map(BudgetAlertThreshold::getThreshold)
                .orElse(0.80); 
            
        } catch (Exception e) {
            return 0.80;
        }
    }

    @Override
    public boolean isExpenseExceedThreshold(Expense expense) {
        Lead lead = expense.getLead();
        Ticket ticket = expense.getTicket();
        Customer customer = null;
        if (lead != null) {
            customer = lead.getCustomer();
        } else if (ticket != null) {
            customer = ticket.getCustomer();
        }

        if (customer == null || customer.getCustomerBudgets() == null || customer.getCustomerBudgets().isEmpty()) {
            return false; 
        }
        
        BigDecimal budgetLimit = BigDecimal.valueOf(customerService.getTotalBudgetByCustomerId(customer));
        int customerId = customer.getCustomerId();
        BigDecimal totalExistingExpenses = BigDecimal.valueOf(expenseService.getTotalExpenseByCustomerId(customerId));
        BigDecimal newExpenseAmount = expense.getAmount() != null ? expense.getAmount() : BigDecimal.ZERO;
        BigDecimal newTotal = totalExistingExpenses.add(newExpenseAmount);

        double threshold = this.getThreshold();
        BigDecimal thresholdLimit = budgetLimit.multiply(BigDecimal.valueOf(threshold));

        return newTotal.compareTo(thresholdLimit) > 0 && newTotal.compareTo(budgetLimit) <= 0;
    }
}
