package site.easy.to.build.crm.service.data;

import java.io.File;

public interface ImportDataService {
    
    public void importData(File f , Class<?> entity) throws Exception;
}

