package site.easy.to.build.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import site.easy.to.build.crm.entity.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    List<Expense> findByExpenseType(Integer expenseType);

    @Query("SELECT e FROM Expense e WHERE e.lead.leadId = :id OR e.ticket.ticketId = :id")
    List<Expense> findByRelatedId(@Param("id") Integer id);

    @Query("SELECT e FROM Expense e WHERE e.expenseType = :type AND (e.lead.leadId = :id OR e.ticket.ticketId = :id)")
    List<Expense> findByExpenseTypeAndRelatedId(@Param("type") Integer type, @Param("id") Integer id);
}
