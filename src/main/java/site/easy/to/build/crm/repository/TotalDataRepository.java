package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.dto.TotalDataDTO;
import site.easy.to.build.crm.entity.CustomerBudget;

@Repository
public interface TotalDataRepository extends JpaRepository<CustomerBudget, Long> {

    @Query("SELECT new site.easy.to.build.crm.dto.TotalDataDTO(" +
           "CAST((SELECT COALESCE(SUM(cb.amount), 0) FROM CustomerBudget cb) AS double), " +
           "CAST((SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.ticket.ticketId IS NOT NULL) AS double), " +
           "CAST((SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.lead.leadId IS NOT NULL) AS double))")
    TotalDataDTO getTotalData();
}