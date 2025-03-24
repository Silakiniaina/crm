package site.easy.to.build.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class TotalDataDTO {
    private double totalBudget;
    private double totalTicketExpense;
    private double totalLeadExpense;

    public TotalDataDTO(double totalBudget, double totalTicketExpense, double totalLeadExpense) {
        this.totalBudget = totalBudget;
        this.totalTicketExpense = totalTicketExpense;
        this.totalLeadExpense = totalLeadExpense;
    }
}
