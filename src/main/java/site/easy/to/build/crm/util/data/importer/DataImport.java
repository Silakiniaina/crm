package site.easy.to.build.crm.util.data.importer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DataImport {
    
    private List<String> errors;
    private boolean valid;
    private String fileName;

    public DataImport() {
        this.errors = new ArrayList<>();
        this.valid = true;
    }

    public void validate(){
        this.checkIntegrity();
        this.checkForeignKey();
    }

    public boolean validateEmail(String email) {
        // Check for null or empty
        if (email == null || email.trim().isEmpty()) {
            errors.add("Email cannot be null or empty");
            valid = false;
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!Pattern.compile(emailRegex).matcher(email).matches()) {
            errors.add("Invalid email format: " + email);
            valid = false;
            return false;
        }

        return true;
    }

    public boolean validateDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            errors.add("Date cannot be null or empty");
            valid = false;
            return false;
        }

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
                
            }
        }

        errors.add("Invalid date format: " + date + ". Supported formats: yyyy-MM-dd, MM/dd/yyyy, dd-MM-yyyy, yyyy/MM/dd");
        valid = false;
        return false;
    }

    public boolean validateNumber(String number, double min) {
        if (number == null || number.trim().isEmpty()) {
            errors.add("Number cannot be null or empty");
            valid = false;
            return false;
        }

        try {
            double value = Double.parseDouble(number);
            if (value < min) {
                errors.add("Number " + number + " is below minimum value " + min);
                valid = false;
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            errors.add("Invalid number format: " + number);
            valid = false;
            return false;
        }
    }

    public boolean validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            errors.add("Status cannot be null or empty");
            valid = false;
            return false;
        }
        List<String> validStatuses = getValidStatus();
        
        boolean isValid = validStatuses.stream()
            .anyMatch(validStatus -> validStatus.equalsIgnoreCase(status.trim()));
        
        if (!isValid) {
            errors.add("Invalid status: " + status + ". Valid statuses are: " + String.join(", ", validStatuses));
            valid = false;
        }

        return isValid;
    }

    public boolean checkRequiredValue(String value, String fieldName) {
        if (value == null) {
            errors.add(fieldName + " cannot be null");
            valid = false;
            return false;
        }

        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            errors.add(fieldName + " cannot be empty");
            valid = false;
            return false;
        }

        return true;
    }

    public boolean checkRequiredValue(String value) {
        return checkRequiredValue(value, "Field");
    }

    public boolean checkEmailUnique(String email, Set<String> emails) {
        if (emails == null) {
            emails = new HashSet<>();
        }

        boolean isUnique = emails.stream()
            .noneMatch(existingEmail -> existingEmail.equalsIgnoreCase(email));

        if (!isUnique) {
            errors.add("Email already exists: " + email);
            valid = false;
            return false;
        }

        emails.add(email);
        return true;
    }

    public boolean checkEmailUnique(String email) {
        return checkEmailUnique(email, new HashSet<>());
    }

    /* Abstract */

    public abstract List<String> getValidStatus();
    public abstract void checkIntegrity();
    public abstract void checkForeignKey();
    public abstract void insertData();

}
