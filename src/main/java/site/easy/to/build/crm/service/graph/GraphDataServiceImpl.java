package site.easy.to.build.crm.service.graph;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GraphDataServiceImpl implements GraphDataService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Map<String, Object>> getBudgetVsExpensesByCustomer(int limit) {
        String query = "SELECT " +
                "c.name AS customer_name, " +
                "COALESCE(SUM(cb.amount), 0) AS total_budget, " +
                "COALESCE(SUM(e.amount), 0) AS total_expenses " +
                "FROM customer c " +
                "LEFT JOIN customer_budget cb ON c.customer_id = cb.customer_id " +
                "LEFT JOIN trigger_lead tl ON c.customer_id = tl.customer_id " +
                "LEFT JOIN trigger_ticket tt ON c.customer_id = tt.customer_id " +
                "LEFT JOIN expenses e ON e.lead_id = tl.lead_id OR e.ticket_id = tt.ticket_id " +
                "GROUP BY c.customer_id, c.name " +
                "LIMIT :limit";

        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery(query)
                .setParameter("limit", limit)
                .getResultList();

        return results.stream()
                .map(row -> Map.of(
                        "customerName", row[0],           
                        "totalBudget", row[1],           
                        "totalExpenses", row[2]          
                ))
                .toList();
    }

    @Override
    public List<Map<String, Object>> getLeadConversionRateByMonth() {
        String query = "SELECT " +
                "DATE_FORMAT(created_at, '%Y-%m') AS month, " +
                "COUNT(CASE WHEN status = 'success' THEN 1 END) AS success, " +
                "COUNT(*) AS total_leads, " +
                "(COUNT(CASE WHEN status = 'success' THEN 1 END) / COUNT(*)) * 100 AS conversion_rate " +
                "FROM trigger_lead " +
                "GROUP BY DATE_FORMAT(created_at, '%Y-%m') " +
                "ORDER BY month";

        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery(query)
                .getResultList();

        return results.stream()
                .map(row -> Map.of(
                        "month", row[0],                  
                        "converted", row[1],             
                        "totalLeads", row[2],             
                        "conversionRate", row[3]         
                ))
                .toList();
    }

    @Override
    public List<Map<String, Object>> getAverageExpensesByType() {
        String query = "SELECT 'Leads' AS type, " +
                "COALESCE(AVG(CASE WHEN lead_id IS NOT NULL THEN amount END), 0) AS avg_expense " +
                "FROM expenses WHERE lead_id IS NOT NULL " +
                "UNION " +
                "SELECT 'Tickets' AS type, " +
                "COALESCE(AVG(CASE WHEN ticket_id IS NOT NULL THEN amount END), 0) AS avg_expense " +
                "FROM expenses WHERE ticket_id IS NOT NULL";

        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery(query)
                .getResultList();

        return results.stream()
                .map(row -> Map.of(
                        "type", row[0],                   
                        "avgExpense", row[1]             
                ))
                .toList();
    }
}
