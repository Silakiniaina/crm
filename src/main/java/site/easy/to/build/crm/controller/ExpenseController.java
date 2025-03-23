package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.service.expense.ExpenseServiceImpl;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;
import site.easy.to.build.crm.service.user.UserServiceImpl;
import site.easy.to.build.crm.util.AuthenticationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseServiceImpl expenseService;
    private final LeadServiceImpl leadService;
    private final TicketServiceImpl ticketService;
    private final UserServiceImpl userService;
    private final AuthenticationUtils authenticationUtils;

    @Autowired
    public ExpenseController(AuthenticationUtils auth,ExpenseServiceImpl expenseService,LeadServiceImpl leadService,TicketServiceImpl ticketService,UserServiceImpl userService) {
        this.expenseService = expenseService;
        this.leadService = leadService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.authenticationUtils = auth;
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

    @PostMapping 
    public String saveExpense(
            @RequestParam("type") Integer type,
            @RequestParam(value = "id", required = false) Integer id,
            @Valid @ModelAttribute("expense") Expense expense,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("type", type);
            model.addAttribute("id", id);
            return "expenses/add";
        }
        
        expense.setExpenseType(type);
        
        int loggedInUserId = authenticationUtils.getLoggedInUserId(authentication);
        if (loggedInUserId == -1) {
            redirectAttributes.addFlashAttribute("errorMessage", "No logged-in user found. Please log in again.");
            return "redirect:/login";
        }

        // Fetch the logged-in user
        User createdBy = userService.findById(loggedInUserId);
        expense.setCreatedBy(createdBy);
        
        if (id != null) {
            if (type == 1) { 
                Lead lead = leadService.findByLeadId(id);
                if (lead != null) {
                    expense.setLead(lead);
                }
            } else if (type == 2) { 
                Ticket ticket = ticketService.findByTicketId(id);
                if (ticket != null) {
                    expense.setTicket(ticket);
                }
            }
        }

        try{
            expenseService.addExpense(expense);
            redirectAttributes.addFlashAttribute("successMessage", "Expense saved successfully.");
            if (type == 1 && id != null) {
                return "redirect:/expenses/add?type=1&id=" + id;
            } else if (type == 2 && id != null) {
                return "redirect:/expenses/add";
            } else {
                return "redirect:/expenses/add"; 
            }
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save expense. Please try again.");
            return "redirect:/expenses/add";
        }
        
    }
}