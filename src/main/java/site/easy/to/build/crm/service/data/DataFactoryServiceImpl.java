package site.easy.to.build.crm.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.repository.DataFactoryRepository;

@Service
public class DataFactoryServiceImpl implements DataFactoryService{

    @Autowired
    private DataFactoryRepository dataFactoryRepository;

    @Override
    public int resetData() {
        return dataFactoryRepository.resetData();
    }
    
}
