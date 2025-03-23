package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.service.expense.ExpenseServiceImpl;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;
import site.easy.to.build.crm.service.user.UserServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseServiceImpl expenseService;
    private final LeadServiceImpl leadService;
    private final TicketServiceImpl ticketService;
    private final UserServiceImpl userService;

    @Autowired
    public ExpenseController(ExpenseServiceImpl expenseService,LeadServiceImpl leadService,TicketServiceImpl ticketService,UserServiceImpl userService) {
        this.expenseService = expenseService;
        this.leadService = leadService;
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping("/add")
    public String showAddExpenseForm(
            @RequestParam("type") Integer type,
            @RequestParam(value = "id", required = false) Integer id,
            Model model) {
        
        Expense expense = new Expense();
        expense.setCreatedAt(LocalDate.now());
        expense.setAmount(BigDecimal.ZERO);
        expense.setExpenseType(type);
        model.addAttribute("expense", expense);
        model.addAttribute("type", type);
        model.addAttribute("id", id);
        return "expense/create-expense";
    }
}