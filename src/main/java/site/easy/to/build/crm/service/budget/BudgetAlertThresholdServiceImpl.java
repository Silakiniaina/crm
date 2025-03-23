package site.easy.to.build.crm.service.budget;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.BudgetAlertThreshold;
import site.easy.to.build.crm.repository.BudgetAlertThresholdRepository;

@Service
public class BudgetAlertThresholdServiceImpl implements BudgetAlertThresholdService {

    private final BudgetAlertThresholdRepository budgetAlertThresholdRepository;

    @Autowired
    public BudgetAlertThresholdServiceImpl(BudgetAlertThresholdRepository budgetAlertThresholdRepository) {
        this.budgetAlertThresholdRepository = budgetAlertThresholdRepository;
    }
    
    @Override
    public double getThreshold() {
        try {
            Optional<BudgetAlertThreshold> thresholdOpt = budgetAlertThresholdRepository.findById(1);
            
            return thresholdOpt
                .map(BudgetAlertThreshold::getThreshold)
                .orElse(0.80); 
            
        } catch (Exception e) {
            return 0.80;
        }
    }
}
