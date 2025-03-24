package site.easy.to.build.crm.service.dashboard;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.dto.TotalDataDTO;
import site.easy.to.build.crm.repository.CustomerBudgetRepository;
import site.easy.to.build.crm.repository.ExpenseRepository;
import site.easy.to.build.crm.repository.TotalDataRepository;

@Service
public class TotalDataServiceImpl implements TotalDataService {

    private final TotalDataRepository totalDataRepository;
    private final CustomerBudgetRepository customerBudgetRepository;
    private final ExpenseRepository expenseRepository;

    @Autowired
    public TotalDataServiceImpl(CustomerBudgetRepository cus, ExpenseRepository exp,TotalDataRepository totalDataRepository) {
        this.totalDataRepository = totalDataRepository;
        this.expenseRepository =exp;
        this.customerBudgetRepository = cus;
    }

    @Override
    public TotalDataDTO getTotalData() {
        return totalDataRepository.getTotalData();
    }

    public List<?> getTotalDataDetails(int type) {
        switch (type) {
            case 0: 
                return customerBudgetRepository.findAll();
            case 1: 
                return expenseRepository.findByTicketTicketIdIsNotNull();
            case 2: 
                return expenseRepository.findByLeadLeadIdIsNotNull();
            default:
                throw new IllegalArgumentException("Invalid type parameter: " + type);
        }
    }
}
