package site.easy.to.build.crm.util.data.importer;

import java.util.List;
import java.util.regex.Pattern;

public abstract class DataImport {
    
    private List<String> errors;
    private boolean valid;
    private String fileName;

    public boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

}
