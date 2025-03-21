package site.easy.to.build.crm.util;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;

import jakarta.persistence.Entity;

public class DataImportUtil {
    
    public static void checkHeaderMatchWithEntityField(File file, Class<?> entityClass) throws Exception {
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Provided class is not an entity");
        }
        Set<String> entityFields = Arrays.stream(entityClass.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
        try (CSVParser parser = new CSVParser(new FileReader(file), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            Set<String> csvHeaders = parser.getHeaderMap().keySet();

            if (!csvHeaders.equals(entityFields)) {
                throw new IllegalArgumentException("CSV headers do not match entity fields! Expected: " + entityFields + " but found: " + csvHeaders);
            }
        }
    }

    public static String generateInsertQuery(Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Provided class is not an entity");
        }
    
        String tableName = entityClass.getSimpleName().toLowerCase();
        Field[] fields = entityClass.getDeclaredFields();
    
        String columnNames = Arrays.stream(fields)
                .map(Field::getName)
                .collect(Collectors.joining(", "));
    
        String valuePlaceholders = Arrays.stream(fields)
                .map(f -> "?")
                .collect(Collectors.joining(", "));
    
        return "INSERT INTO " + tableName + " (" + columnNames + ") VALUES (" + valuePlaceholders + ")";
    }

    private static Object convertValue(String value, Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else {
            return value; // Default to String
        }
    }
}
