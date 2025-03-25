package site.easy.to.build.crm.util.data.importer;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDataImport extends DataImport{

    private String email;
    private String name;

    @Override
    public void checkForeignKey() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void checkIntegrity() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<String> getValidStatus() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
