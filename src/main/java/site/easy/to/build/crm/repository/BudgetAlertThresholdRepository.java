package site.easy.to.build.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.easy.to.build.crm.entity.BudgetAlertThreshold;

@Repository
public interface BudgetAlertThresholdRepository extends JpaRepository<BudgetAlertThreshold, Integer> {

    Optional<BudgetAlertThreshold> findFirstByOrderByIdAsc();
    
}
