package site.easy.to.build.crm.service.data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.exception.DataImportException;
import site.easy.to.build.crm.repository.*;
import site.easy.to.build.crm.util.AuthenticationUtils;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.data.importer.DataImporter;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DataImportService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerLoginInfoRepository customerLoginInfoRepository;
    private final LeadRepository leadRepository;
    private final TicketRepository ticketRepository;
    private final ExpenseRepository expenseRepository;
    private final CustomerBudgetRepository customerBudgetRepository;
    private final AuthenticationUtils authenticationUtils;
    private final UserService userService;

    @Autowired
    public DataImportService(CustomerBudgetRepository cust,
                            ExpenseRepository exp, 
                            CustomerRepository customerRepository,
                            UserRepository userRepository,
                            RoleRepository roleRepository,
                            CustomerLoginInfoRepository customerLoginInfoRepository,
                            LeadRepository leadRepository,
                            TicketRepository ticketRepository,
                            AuthenticationUtils authenticationUtils,
                            UserService userService) {
        this.customerRepository = customerRepository;
        this.customerBudgetRepository = cust;
        this.expenseRepository = exp;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerLoginInfoRepository = customerLoginInfoRepository;
        this.leadRepository = leadRepository;
        this.ticketRepository = ticketRepository;
        this.authenticationUtils = authenticationUtils;
        this.userService = userService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, List<String>> importData(MultipartFile customerFile,
                                              MultipartFile budgetFile,
                                              MultipartFile expenseFile,
                                              Authentication authentication) throws Exception {
        int loggedInUserId = authenticationUtils.getLoggedInUserId(authentication);
        User currentUser = userService.findById(loggedInUserId);

        List<CSVRecord> customerRecords = customerFile != null && !customerFile.isEmpty()
            ? extractCsvRecords(customerFile)
            : Collections.emptyList();

        List<CSVRecord> budgetRecords = budgetFile != null && !budgetFile.isEmpty()
            ? extractCsvRecords(budgetFile)
            : Collections.emptyList();

        List<CSVRecord> expenseRecords = expenseFile != null && !expenseFile.isEmpty()
            ? extractCsvRecords(expenseFile)
            : Collections.emptyList();

        String customerFileName = customerFile != null && !customerFile.isEmpty()
            ? customerFile.getOriginalFilename()
            : "customers.csv";

        String budgetFileName = budgetFile != null && !budgetFile.isEmpty()
            ? budgetFile.getOriginalFilename()
            : "budgets.csv";

        String expenseFileName = expenseFile != null && !expenseFile.isEmpty()
            ? expenseFile.getOriginalFilename()
            : "expenses.csv";

        DataImporter dataImporter = new DataImporter(
            customerRecords, customerFileName,
            budgetRecords, budgetFileName,
            expenseRecords, expenseFileName,
            customerRepository, userRepository, roleRepository, customerLoginInfoRepository, leadRepository,customerBudgetRepository,expenseRepository, ticketRepository, currentUser
        );

        // Perform all imports within the transaction
        dataImporter.importCustomer();
        dataImporter.importBudget();
        dataImporter.importExpense();

        Map<String, List<String>> errorMap = dataImporter.getErrorMap();
        if (!errorMap.isEmpty()) {
            throw new DataImportException(errorMap,"Error while importing data");
        }
        return errorMap;
    }

    private List<CSVRecord> extractCsvRecords(MultipartFile file) throws Exception {
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            return parser.getRecords();
        }
    }
}