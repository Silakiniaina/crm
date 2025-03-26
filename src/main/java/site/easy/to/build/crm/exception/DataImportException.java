package site.easy.to.build.crm.exception;

import java.util.List;
import java.util.Map;

public class DataImportException extends Exception{

    Map<String, List<String>> error;
    
    
    public DataImportException(Map<String, List<String>> error,String msn){
        super(msn);
        this.setError(error);
    }

    public Map<String, List<String>> getError() {
        return error;
    }

    public void setError(Map<String, List<String>> error) {
        this.error = error;
    }

    
}
