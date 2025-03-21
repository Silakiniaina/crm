package site.easy.to.build.crm.service.data;

import java.io.File;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import site.easy.to.build.crm.util.DataImportUtil;

@Service
public class ImportDataServiceImpl implements ImportDataService{
    
    @Override
    @Transactional
    public void importData(File f , Class<?> entity) throws Exception{
        DataImportUtil.importData(f, entity);
    }
}
