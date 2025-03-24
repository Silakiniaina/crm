package site.easy.to.build.crm.service.dashboard;

import java.util.List;

import site.easy.to.build.crm.dto.TotalDataDTO;

public interface TotalDataService {
    TotalDataDTO getTotalData();
    List<?> getTotalDataDetails(int type);
}
