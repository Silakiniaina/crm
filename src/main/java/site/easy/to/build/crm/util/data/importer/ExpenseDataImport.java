package site.easy.to.build.crm.util.data.importer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.ExpenseRepository;
import site.easy.to.build.crm.repository.LeadRepository;
import site.easy.to.build.crm.repository.TicketRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
public class ExpenseDataImport extends DataImport {

    private String email;
    private String subjectOrName;
    private String type;
    private String status;
    private String expense;

    private CustomerRepository customerRepository;
    private LeadRepository leadRepository;
    private TicketRepository ticketRepository;
    private ExpenseRepository expenseRepository;
    private User currentUser;
    private Set<String> existingEmails;

    @Autowired
    public ExpenseDataImport(ExpenseRepository exp,CustomerRepository customerRepository,
                            LeadRepository leadRepository,
                            TicketRepository ticketRepository,
                            User currentUser) {
        this.expenseRepository = exp;
        this.customerRepository = customerRepository;
        this.leadRepository = leadRepository;
        this.ticketRepository = ticketRepository;
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
    public List<String> getValidStatus() {
        if ("lead".equalsIgnoreCase(type)) {
            return Arrays.asList("meeting-to-schedule", "scheduled", "archived", "success", "assign-to-sales");
        } else if ("ticket".equalsIgnoreCase(type)) {
            return Arrays.asList("open", "assigned", "on-hold", "in-progress", "resolved", "closed", 
                                "reopened", "pending-customer-response", "escalated", "archived");
        }
        return Collections.emptyList();
    }

    @Override
    public void checkIntegrity() {
        checkRequiredValue(email, "customer_email");
        validateEmail(email);
        
        checkRequiredValue(subjectOrName, "subject_or_name");
        
        checkRequiredValue(type, "type");
        if (!"lead".equalsIgnoreCase(type) && !"ticket".equalsIgnoreCase(type)) {
            this.getErrors().add("Type must be either 'lead' or 'ticket' at line " + getLineNumber());
            this.setValid(false);
        }

        checkRequiredValue(status, "status");
        validateStatus(status);

        checkRequiredValue(expense, "expense");
        validateNumber(expense, 0);
    }

    @Override
    public void checkForeignKey() {
        if (!existingEmails.contains(email.toLowerCase())) {
            this.getErrors().add("Customer with email " + email + " does not exist at line " + getLineNumber());
            this.setValid(false);
        }
    }

    @Override
    public void insertData() {
        if (!isValid()) {
            return;
        }
        Expense expense = toExpense();
        if (expense != null) {
            if (expense.getLead() != null) {
                leadRepository.save(expense.getLead());
            } else if (expense.getTicket() != null) {
                ticketRepository.save(expense.getTicket());
            }
            expenseRepository.save(expense);
        }
    }

    public Expense toExpense() {
        if (!isValid()) {
            return null;
        }

        Expense expenseObject = new Expense();
        expenseObject.setCreatedAt(LocalDate.now());
        expenseObject.setLabel(subjectOrName);
        expenseObject.setDescription(generateDescription());
        expenseObject.setAmount(new BigDecimal(expense));
        expenseObject.setExpenseType(generateExpenseType());
        expenseObject.setCreatedBy(currentUser);

        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            this.getErrors().add("Customer not found for email: " + email + " during insertion");
            this.setValid(false);
            return null;
        }

        if ("lead".equalsIgnoreCase(type)) {
            Lead lead = generateLead(customer);
            expenseObject.setLead(lead);
        } else if ("ticket".equalsIgnoreCase(type)) {
            Ticket ticket = generateTicket(customer);
            expenseObject.setTicket(ticket);
        }

        return expenseObject;
    }

    private String generateDescription() {
        return "Expense imported from CSV for " + type + ": " + subjectOrName;
    }

    private int generateExpenseType() {
        // 0 for lead, 1 for ticket
        return "lead".equalsIgnoreCase(type) ? 1 : 2;
    }

    private Lead generateLead(Customer customer) {
        Lead lead = new Lead();
        lead.setName(subjectOrName);
        lead.setStatus(status);
        lead.setPhone(generatePhoneNumber());
        lead.setGoogleDrive(false);
        lead.setManager(currentUser);
        lead.setEmployee(currentUser);
        lead.setCustomer(customer);
        lead.setCreatedAt(java.time.LocalDateTime.now());
        return lead;
    }

    private Ticket generateTicket(Customer customer) {
        Ticket ticket = new Ticket();
        ticket.setSubject(subjectOrName);
        ticket.setDescription(generateDescription());
        ticket.setStatus(status);
        ticket.setPriority(generatePriority());
        ticket.setManager(currentUser);
        ticket.setEmployee(currentUser);
        ticket.setCustomer(customer);
        ticket.setCreatedAt(java.time.LocalDateTime.now());
        return ticket;
    }

    private String generatePhoneNumber() {
        return String.format("+1-%03d-%03d-%04d",
            new Random().nextInt(1000),
            new Random().nextInt(1000),
            new Random().nextInt(10000)
        );
    }

    private String generatePriority() {
        String[] priorities = {"low", "medium", "high", "urgent", "critical"};
        return priorities[new Random().nextInt(priorities.length)];
    }
}