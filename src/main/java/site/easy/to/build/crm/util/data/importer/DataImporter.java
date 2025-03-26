package site.easy.to.build.crm.util.data.importer;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.*;

import java.util.*;

public class DataImporter {

    private List<CSVRecord> customerCSVRecords;
    private List<CSVRecord> budgetCSVRecords;
    private List<CSVRecord> expenseCSVRecords;
    private Map<String, List<String>> errorMap;
    private Set<String> processedEmails;

    private String customerFileName;
    private String budgetFileName;
    private String expenseFileName;

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerLoginInfoRepository customerLoginInfoRepository;
    private final LeadRepository leadRepository;
    private final TicketRepository ticketRepository;
    private final CustomerBudgetRepository customerBudgetRepository;
    private final ExpenseRepository expenseRepository;
    private final User currentUser;

    @Autowired
    public DataImporter(List<CSVRecord> customerCSVRecords, String customerFileName,
                        List<CSVRecord> budgetCSVRecords, String budgetFileName,
                        List<CSVRecord> expenseCSVRecords, String expenseFileName,
                        CustomerRepository customerRepository,
                        UserRepository userRepository,
                        RoleRepository roleRepository,
                        CustomerLoginInfoRepository customerLoginInfoRepository,
                        LeadRepository leadRepository,
                        CustomerBudgetRepository cust,
                        ExpenseRepository exp,
                        TicketRepository ticketRepository,
                        User currentUser) {
        this.customerCSVRecords = customerCSVRecords;
        this.customerFileName = customerFileName;
        this.budgetCSVRecords = budgetCSVRecords;
        this.budgetFileName = budgetFileName;
        this.expenseCSVRecords = expenseCSVRecords;
        this.expenseFileName = expenseFileName;
        this.errorMap = new HashMap<>();
        this.processedEmails = new HashSet<>();
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.expenseRepository = exp;
        this.customerBudgetRepository = cust;
        this.customerLoginInfoRepository = customerLoginInfoRepository;
        this.leadRepository = leadRepository;
        this.ticketRepository = ticketRepository;
        this.currentUser = currentUser;
    }

    public void importCustomer() {
        CustomerDataImport customerImport = new CustomerDataImport(customerRepository, userRepository, roleRepository, customerLoginInfoRepository, currentUser);
        
        for (CSVRecord record : customerCSVRecords) {
            customerImport.setEmail(record.get("customer_email"));
            customerImport.setName(record.get("customer_name"));
            customerImport.setLineNumber(record.getRecordNumber());
            customerImport.validate();
            
            if (customerImport.isValid()) {
                customerImport.insertData(); // Persist here; rollback will handle failures
                processedEmails.add(customerImport.getEmail().toLowerCase());
            }
            
            if (!customerImport.getErrors().isEmpty()) {
                errorMap.computeIfAbsent(customerFileName, k -> new ArrayList<>())
                        .addAll(customerImport.getErrors());
            }
        }
    }

    public void importBudget() {
        BudgetDataImport budgetImport = new BudgetDataImport(customerBudgetRepository,customerRepository, currentUser);
        budgetImport.setCustomerImportEmails(processedEmails);

        for (CSVRecord record : budgetCSVRecords) {
            budgetImport.setEmail(record.get("customer_email"));
            budgetImport.setBudget(record.get("Budget"));
            budgetImport.setLineNumber(record.getRecordNumber());
            budgetImport.validate();

            if (budgetImport.isValid()) {
                budgetImport.insertData(); // Persist here; rollback will handle failures
            }

            if (!budgetImport.getErrors().isEmpty()) {
                errorMap.computeIfAbsent(budgetFileName, k -> new ArrayList<>())
                        .addAll(budgetImport.getErrors());
            }
        }
    }

    public void importExpense() {
        ExpenseDataImport expenseImport = new ExpenseDataImport(expenseRepository,customerRepository, leadRepository, ticketRepository, currentUser);
        expenseImport.setCustomerImportEmails(processedEmails);

        for (CSVRecord record : expenseCSVRecords) {
            expenseImport.setEmail(record.get("customer_email"));
            expenseImport.setSubjectOrName(record.get("subject_or_name"));
            expenseImport.setType(record.get("type"));
            expenseImport.setStatus(record.get("status"));
            expenseImport.setExpense(record.get("expense"));
            expenseImport.setLineNumber(record.getRecordNumber());
            expenseImport.validate();

            if (expenseImport.isValid()) {
                expenseImport.insertData(); // Persist here; rollback will handle failures
            }

            if (!expenseImport.getErrors().isEmpty()) {
                errorMap.computeIfAbsent(expenseFileName, k -> new ArrayList<>())
                        .addAll(expenseImport.getErrors());
            }
        }
    }

    public Map<String, List<String>> getErrorMap() {
        return errorMap;
    }
}