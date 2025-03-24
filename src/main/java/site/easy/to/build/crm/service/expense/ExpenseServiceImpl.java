package site.easy.to.build.crm.service.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerBudget;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.exception.BudgetOverrunException;
import site.easy.to.build.crm.repository.ExpenseRepository;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CustomerServiceImpl customerService;

    @Autowired
    public ExpenseServiceImpl(CustomerServiceImpl customerService,ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
        this.customerService = customerService;
    }

    @Override
    public Expense addExpense(Expense expense, boolean isValidated) throws BudgetOverrunException,Exception {
        if (expense == null) {
            throw new Exception("Expense cannot be null");
        }
        try {

            // Determine customer ID from Lead or Ticket, if present
            Customer customer = null;
            if (expense.getLead() != null && expense.getLead().getCustomer() != null) {
                customer = expense.getLead().getCustomer();
            } else if (expense.getTicket() != null && expense.getTicket().getCustomer() != null) {
                customer = expense.getTicket().getCustomer();
            }
    
            // If a customer ID is found, check expenses against budget
            if ( customer != null && !isValidated) {
                double totalExpenseAmount = this.getTotalExpenseByCustomerId(customer.getCustomerId().intValue()) + expense.getAmount().doubleValue();
                double totalBudget = customerService.getTotalBudgetByCustomerId(customer);
                
                System.out.println("Expenses : "+totalExpenseAmount+" - total budgets : "+totalBudget);
                if (totalExpenseAmount > totalBudget) {
                    throw new BudgetOverrunException("Total expenses (" + totalExpenseAmount + 
                        ") exceed the budget (" + totalBudget + ") for customer ID " + customer.getCustomerId());
                }
            }
            return expenseRepository.save(expense);
        } catch (BudgetOverrunException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("Error adding expense: " + e.getMessage());
        }
    }

    @Override
    public List<Expense> findExpensesByFilters(Integer type, Integer id) {
        if (type != null && id != null) {
            return expenseRepository.findByExpenseTypeAndRelatedId(type, id);
        } else if (type != null) {
            return expenseRepository.findByExpenseType(type);
        } else if (id != null) {
            return expenseRepository.findByRelatedId(id);
        } else {
            return expenseRepository.findAll();
        }
    }

    @Override
    public double getTotalExpenseByCustomerId(int cusId) {
        BigDecimal leadTotal = expenseRepository.findTotalLeadExpenseByCustomerId(cusId);
        BigDecimal ticketTotal = expenseRepository.findTotalTicketExpenseByCustomerId(cusId);
        return leadTotal.add(ticketTotal).doubleValue();
    }

    @Override
    public void validateBudgetOverrun(Expense expense) throws Exception {
        addExpense(expense, true);
    }

    @Override
    public Expense findById(int id) {
        return expenseRepository.findById(id).orElse(null);
    }
}
