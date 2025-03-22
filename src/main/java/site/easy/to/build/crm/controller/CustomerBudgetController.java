package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.service.customer.CustomerService;

@Controller
public class CustomerBudgetController {

    private final CustomerService customerService;

    public CustomerBudgetController(CustomerService customerService) {
        this.customerService = customerService;
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
        return "budget/customerBudget";
    }

}
