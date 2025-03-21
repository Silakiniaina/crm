package site.easy.to.build.crm.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseUtil {

    public static List<String> getColumnNames(EntityManager entityManager, Class<?> entityClass) {
        List<String> columnNames = new ArrayList<>();
        Metamodel metamodel = entityManager.getMetamodel();
        EntityType<?> entityType = metamodel.entity(entityClass);

        for (SingularAttribute<?, ?> attribute : entityType.getSingularAttributes()) {
            if (!attribute.isAssociation() && !attribute.isCollection() && !attribute.isId() &&
                    !attribute.getName().contains("google") && !attribute.getName().contains("createdAt")) {
                String word = attribute.getName();
                String[] words = splitByCamelCase(word);
                word = capitalizeFirst(words);
                columnNames.add(word);
            }
        }

        return columnNames;
    }

    public static Set<Class<?>> getAllEntitiesWithTriggerTable(EntityManager entityManager) {
        Set<Class<?>> entitiesWithTriggerTable = new HashSet<>();

        Metamodel metamodel = entityManager.getMetamodel();

        for (EntityType<?> entityType : metamodel.getEntities()) {
            Class<?> entityClass = entityType.getJavaType();
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            if (tableAnnotation != null && tableAnnotation.name().startsWith("trigger")) {
                entitiesWithTriggerTable.add(entityClass);
            }
        }

        return entitiesWithTriggerTable;
    }
    private static String[] splitByCamelCase(String input) {
        return input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    }

    private static String capitalizeFirst(String[] words) {
        StringBuilder sb = new StringBuilder();
        for (String s : words) {
            if(s.equals("Id")) {
                continue;
            }
            String word = s.toLowerCase();
            sb.append(word).append(" ");
        }
        return sb.toString().trim();
    }

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/eval_n1_crm?useSSL=false&serverTimezone=UTC", "root", "DashDashGo2K23!!");
        } 
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();  
        }
        
        return con;
    }
}
