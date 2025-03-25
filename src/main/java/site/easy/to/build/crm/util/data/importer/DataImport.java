package site.easy.to.build.crm.util.data.importer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public boolean validateDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }

        try {
            // Try parsing with multiple common date formats
            List<String> dateFormats = Arrays.asList(
                "yyyy-MM-dd",
                "MM/dd/yyyy",
                "dd-MM-yyyy",
                "yyyy/MM/dd"
            );

            for (String format : dateFormats) {
                try {
                    LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
                    return true;
                } catch (DateTimeParseException e) {
                    // Continue to next format
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateNumber(String number, double min) {
        if (number == null || number.trim().isEmpty()) {
            return false;
        }

        try {
            double value = Double.parseDouble(number);
            return value >= min;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    

    /* Abstract */

    public abstract List<String> getValidStatus();

}
