package site.easy.to.build.crm.service.data;

import org.springframework.stereotype.Service;

import site.easy.to.build.crm.repository.data.DataFactoryRepository;

@Service
public class DataFactoryServiceImpl implements DataFactoryService{

    private final DataFactoryRepository dataFactoryRepository;

    public DataFactoryServiceImpl(DataFactoryRepository dataFactoryRepository) {
        this.dataFactoryRepository = dataFactoryRepository;
    }

    @Override
    public int resetData() {
        return dataFactoryRepository.resetData();
    }
    
}
