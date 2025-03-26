package site.easy.to.build.crm.controller;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerBudget;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.service.budget.CustomerBudgetServiceImpl;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;

@Controller
public class CustomerBudgetController {

    private final CustomerService customerService;
    private final CustomerBudgetServiceImpl budgetService;
    private final UserService userService; 
    private final AuthenticationUtils authenticationUtils; 

    @Autowired
    public CustomerBudgetController(CustomerService customerService, CustomerBudgetServiceImpl budgetService, UserService userService, AuthenticationUtils authenticationUtils) {
        this.customerService = customerService;
        this.budgetService = budgetService;
        this.userService = userService;
        this.authenticationUtils = authenticationUtils;
    }

    @GetMapping("/customers/{id}/budgets")
    public String getCustomerBudgets(Model model, 
                                @PathVariable("id") int customerId,
                                RedirectAttributes redirectAttributes) {
        Customer customer = customerService.findByCustomerId(customerId);
        
        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Customer with ID " + customerId + " not found");
            return "redirect:/error/404";  // Redirect to error page
        }
        
        model.addAttribute("customerBudgets", customer.getCustomerBudgets());
        model.addAttribute("customerId", customer.getCustomerId());
        model.addAttribute("totalBudget", customerService.getTotalBudgetByCustomerId(customer));
        return "budget/customerBudget";
    }

    @GetMapping("/customers/{id}/budgets/add")
    public String showCreateBudgetForm(@PathVariable("id") int customerId, Model model, RedirectAttributes redirectAttributes) {

        Customer customer = customerService.findByCustomerId(customerId);
        
        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Customer with ID " + customerId + " not found");
            return "redirect:/error/404";  
        }

        model.addAttribute("customerBudget", new CustomerBudget());
        model.addAttribute("customer", customer);
        return "budget/addBudget";
    }

    @PostMapping("/customers/{id}/budgets/add")
    public String createBudget(@PathVariable("id") Integer customerId,
                            @ModelAttribute("customerBudget") @Valid CustomerBudget budget,
                            BindingResult result,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        // Check if user is authenticated
        int loggedInUserId = authenticationUtils.getLoggedInUserId(authentication);
        if (loggedInUserId == -1) {
            redirectAttributes.addFlashAttribute("errorMessage", "No logged-in user found. Please log in again.");
            return "redirect:/login";
        }

        // Fetch the logged-in user
        User createdBy = userService.findById(loggedInUserId);
        if (createdBy == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Logged-in user not found in database.");
            return "redirect:/login";
        }

        // Fetch the customer
        Customer customer = customerService.findByCustomerId(customerId);
        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Customer with ID " + customerId + " not found.");
            return "redirect:/customers";
        }

        // Validate the budget object
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid budget data provided.");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.customerBudget", result);
            redirectAttributes.addFlashAttribute("customerBudget", budget);
            return "redirect:/customers/" + customerId + "/budgets/add";
        }

        // Set budget properties
        budget.setCustomer(customer);
        budget.setCreatedBy(createdBy);
        budget.setCreatedAt(Date.valueOf(LocalDate.now()));
        
        // Save the budget
        try {
            if(budget.getAmount() < 0){
                throw new Exception("Amount for budget should be positive");
            }
            budgetService.addBudget(budget);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create budget: " + e.getMessage());
            return "redirect:/customers/" + customerId + "/budgets/add";
        }

        // Success case
        redirectAttributes.addFlashAttribute("successMessage", "Budget created successfully!");
        return "redirect:/customers/" + customerId + "/budgets";
    }
}
