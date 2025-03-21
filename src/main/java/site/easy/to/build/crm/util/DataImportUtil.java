package site.easy.to.build.crm.util;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

public class DataImportUtil {

    public static void importData(File file, Class<?> entityClass) throws Exception {
        checkHeaderMatchWithEntityField(file, entityClass);
        String insertQuery = generateInsertQuery(entityClass);
        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement stmt = connection.prepareStatement(insertQuery);
            CSVParser parser = new CSVParser(new FileReader(file), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            connection.setAutoCommit(false);
            for (CSVRecord record : parser) {
                Object entity = createEntityInstance(entityClass, record);
                addEntityIntoInsertQuery(stmt, entity);
                stmt.addBatch();
            }
            stmt.executeBatch(); 
            connection.commit(); 
            System.out.println("Data import successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void checkHeaderMatchWithEntityField(File file, Class<?> entityClass) throws Exception {
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Provided class is not an entity");
        }
        Set<String> entityFields = Arrays.stream(entityClass.getDeclaredFields())
                .map(Field::getName)
                .filter(fieldName -> !fieldName.equals("id"))
                .collect(Collectors.toSet());
        try (CSVParser parser = new CSVParser(new FileReader(file), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            Set<String> csvHeaders = parser.getHeaderMap().keySet();
            Set<String> csvHeadersWithoutId = new HashSet<>(csvHeaders);
            csvHeadersWithoutId.remove("id");
            if (!csvHeadersWithoutId.equals(entityFields)) {
                throw new IllegalArgumentException("CSV headers do not match entity fields! Expected: " + 
                        entityFields + " but found: " + csvHeadersWithoutId);
            }
        }
    }

    @SuppressWarnings("removal")
    public static String generateInsertQuery(Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Provided class is not an entity");
        }
        String tableName;
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            tableName = tableAnnotation.name();
            if (tableName == null || tableName.isEmpty()) {
                tableName = entityClass.getSimpleName().toLowerCase();
            }
        } else {
            tableName = entityClass.getSimpleName().toLowerCase();
        }
        List<Field> fieldsWithoutId = Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> !field.getName().equals("id"))
                .collect(Collectors.toList());
        String columnNames = fieldsWithoutId.stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));
        String valuePlaceholders = fieldsWithoutId.stream()
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
            if (fieldName.equals("id")) {
                continue;
            }
            if (!record.isMapped(fieldName)) {
                continue;
            }
            String csvValue = record.get(fieldName);
            String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method setter = entityClass.getMethod(setterName, field.getType());
            Object convertedValue = convertValue(csvValue, field.getType());
            setter.invoke(entity, convertedValue);
        }
        return entity;
    }

    public static void addEntityIntoInsertQuery(PreparedStatement stmt, Object entity) throws Exception {
        List<Field> fieldsWithoutId = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> !field.getName().equals("id"))
                .collect(Collectors.toList());
        for (int i = 0; i < fieldsWithoutId.size(); i++) {
            Field field = fieldsWithoutId.get(i);
            String fieldName = field.getName();
            String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method getter = entity.getClass().getMethod(getterName);
            Object value = getter.invoke(entity);
            stmt.setObject(i + 1, value);
        }
    }
}
