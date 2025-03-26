package site.easy.to.build.crm.service.graph;

import java.util.List;
import java.util.Map;

public interface GraphDataService {

    List<Map<String, Object>> getBudgetVsExpensesByCustomer(int limit);

    List<Map<String, Object>> getLeadConversionRateByMonth();

    List<Map<String, Object>> getAverageExpensesByType();
}