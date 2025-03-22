package site.easy.to.build.crm.service.budget;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import site.easy.to.build.crm.entity.CustomerBudget;
import site.easy.to.build.crm.repository.CustomerBudgetRepository;

@Service
@AllArgsConstructor
public class CustomerBudgetServiceImpl implements CustomerBudgetService {
    
    private final CustomerBudgetRepository customerBudgetRepository;
    
    @Override
    public CustomerBudget addBudget(CustomerBudget customerBudget) throws Exception {
        return customerBudgetRepository.save(customerBudget);
    }
}
