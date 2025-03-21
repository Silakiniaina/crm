package site.easy.to.build.crm.util;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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


    public static Object createEntityInstance(Class<?> entityClass, CSVRecord record) throws Exception {
        Object entity = entityClass.getDeclaredConstructor().newInstance();

        for (Field field : entityClass.getDeclaredFields()) {
            String fieldName = field.getName();
            String csvValue = record.get(fieldName);

            String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method setter = entityClass.getMethod(setterName, field.getType());

            Object convertedValue = convertValue(csvValue, field.getType());
            setter.invoke(entity, convertedValue);
        }

        return entity;
    }

    public static void addEntityIntoInsertQuery(PreparedStatement stmt, Object entity) throws Exception {
        Field[] fields = entity.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            
            String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method getter = entity.getClass().getMethod(getterName);
            
            Object value = getter.invoke(entity);
            stmt.setObject(i + 1, value);
        }
    }
}
