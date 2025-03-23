package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import site.easy.to.build.crm.exception.BudgetOverrunException;
import site.easy.to.build.crm.service.expense.ExpenseServiceImpl;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;
import site.easy.to.build.crm.service.user.UserServiceImpl;
import site.easy.to.build.crm.util.AuthenticationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseServiceImpl expenseService;
    private final LeadServiceImpl leadService;
    private final TicketServiceImpl ticketService;
    private final UserServiceImpl userService;
    private final AuthenticationUtils authenticationUtils;

    @Autowired
    public ExpenseController(AuthenticationUtils auth, ExpenseServiceImpl expenseService, LeadServiceImpl leadService, TicketServiceImpl ticketService, UserServiceImpl userService) {
        this.expenseService = expenseService;
        this.leadService = leadService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.authenticationUtils = auth;
    }

    @GetMapping
    public String getAllExpenses(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer id,
            Model model) {

        List<Expense> expenses = expenseService.findExpensesByFilters(type, id);
        model.addAttribute("expenses", expenses);
        model.addAttribute("type", type);

        return "expense/show-all-expenses";
    }

    @GetMapping("/add")
    public String showAddExpenseForm(
            @RequestParam("type") Integer type,
            @RequestParam(value = "id", required = false) Integer id,
            Model model) {
        Object budgetOverrunObj = model.asMap().get("budgetOverrun");
        Boolean budgetOverrun = budgetOverrunObj instanceof Boolean ? (Boolean) budgetOverrunObj : false;
        Object redirectedExpenseObj = model.asMap().get("expense");
        Expense expense = redirectedExpenseObj instanceof Expense ? (Expense) redirectedExpenseObj : new Expense();
        if (redirectedExpenseObj == null) {
            expense.setCreatedAt(LocalDate.now());
            expense.setAmount(BigDecimal.ZERO);
            expense.setExpenseType(type);
        }
        model.addAttribute("expense", expense);
        model.addAttribute("type", type);
        model.addAttribute("id", id);
        model.addAttribute("budgetOverrun", budgetOverrun);

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
            model.addAttribute("budgetOverrun", false); 
            return "expense/create-expense";
        }
        expense.setExpenseType(type);
        int loggedInUserId = authenticationUtils.getLoggedInUserId(authentication);
        if (loggedInUserId == -1) {
            redirectAttributes.addFlashAttribute("errorMessage", "No logged-in user found. Please log in again.");
            redirectAttributes.addFlashAttribute("budgetOverrun", false); 
            return "redirect:/login";
        }
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
        try {
            expenseService.addExpense(expense, false);
            redirectAttributes.addFlashAttribute("successMessage", "Expense saved successfully.");
            redirectAttributes.addFlashAttribute("budgetOverrun", false); 
            if (type == 1 && id != null) {
                return "redirect:/expenses/add?type=1&id=" + id;
            } else if (type == 2 && id != null) {
                return "redirect:/expenses/add?type=2&id=" + id;
            } else {
                return "redirect:/expenses/add?type=" + type + "&id=" + id;
            }
        } catch (BudgetOverrunException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("budgetOverrun", true); 
            redirectAttributes.addFlashAttribute("expense", expense); 
            return "redirect:/expenses/add?type=" + type + "&id=" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save expense. Please try again. " + e.getMessage());
            redirectAttributes.addFlashAttribute("budgetOverrun", false); 
            return "redirect:/expenses/add?type=" + type + "&id=" + id;
        }
    }

    private String buildRedirectUrl(Expense expense) {
        Integer type = expense.getExpenseType();
        Integer id = null;

        if (type != null) {
            if (type == 1 && expense.getLead() != null) {
                id = expense.getLead().getLeadId();
            } else if (type == 2 && expense.getTicket() != null) {
                id = expense.getTicket().getTicketId();
            }
        }

        String redirectType = type != null ? type.toString() : "1";
        String redirectId = id != null ? "&id=" + id : "";

        return "redirect:/expenses/add?type=" + redirectType + redirectId;
    }
}