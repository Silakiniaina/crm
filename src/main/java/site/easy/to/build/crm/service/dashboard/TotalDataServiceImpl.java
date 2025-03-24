package site.easy.to.build.crm.service.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.dto.TotalDataDTO;
import site.easy.to.build.crm.repository.TotalDataRepository;

@Service
public class TotalDataServiceImpl implements TotalDataService {

    private final TotalDataRepository totalDataRepository;

    @Autowired
    public TotalDataServiceImpl(TotalDataRepository totalDataRepository) {
        this.totalDataRepository = totalDataRepository;
    }

    @Override
    public TotalDataDTO getTotalData() {
        return totalDataRepository.getTotalData();
    }
}
