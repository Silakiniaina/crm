package site.easy.to.build.crm.util.data.importer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerBudget;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.CustomerBudgetRepository;
import site.easy.to.build.crm.repository.CustomerRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class BudgetDataImport extends DataImport {

    private String email;
    private String budget;
    private CustomerRepository customerRepository;
    private CustomerBudgetRepository customerBudgetRepository;
    private User currentUser; 
    private Set<String> existingEmails; 

    @Autowired
    public BudgetDataImport(CustomerBudgetRepository customerbr,CustomerRepository customerRepository, User currentUser) {
        this.customerRepository = customerRepository;
        this.customerBudgetRepository = customerbr;
        this.currentUser = currentUser;
        this.existingEmails = new HashSet<>();
        loadExistingEmails();
    }

    public void setCustomerImportEmails(Set<String> customerImportEmails) {
        if (customerImportEmails != null) {
            this.existingEmails.addAll(customerImportEmails);
        }
    }

    private void loadExistingEmails() {
        List<Customer> customers = customerRepository.findAll();
        customers.forEach(customer -> existingEmails.add(customer.getEmail().toLowerCase()));
    }

    @Override
    public void checkIntegrity() {
        this.checkRequiredValue(this.getEmail(), "customer_email");
        this.validateEmail(this.getEmail());
        this.checkRequiredValue(this.getBudget(), "Budget");
        this.validateNumber(this.getBudget(), 0); 
    }

    @Override
    public void checkForeignKey() {
        if (!existingEmails.contains(this.getEmail().toLowerCase())) {
            this.getErrors().add("Customer with email " + this.getEmail() + " does not exist at line " + this.getLineNumber());
            this.setValid(false);
        }
    }

    @Override
    public List<String> getValidStatus() {
        return new ArrayList<>(); 
    }

    @Override
    public void insertData() {
        if (!isValid()) {
            return;
        }
        CustomerBudget customerBudget = toCustomerBudget();
        if (customerBudget != null) {
            customerBudgetRepository.save(customerBudget);
        }
    }

    public CustomerBudget toCustomerBudget() {
        if (!this.isValid()) {
            return null;
        }
        CustomerBudget customerBudget = new CustomerBudget();
        customerBudget.setAmount(Double.parseDouble(this.getBudget()));
        customerBudget.setCreatedAt(Date.valueOf(LocalDate.now()));
        customerBudget.setCreatedBy(this.currentUser);
        Customer customer = customerRepository.findByEmail(this.getEmail());
        if (customer != null) {
            customerBudget.setCustomer(customer);
        }
        return customerBudget;
    }
}