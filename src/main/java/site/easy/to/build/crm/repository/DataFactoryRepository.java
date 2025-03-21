package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DataFactoryRepository {
    
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = 
           "SET FOREIGN_KEY_CHECKS = 0;" +
           
           // Store admin user IDs
           "CREATE TEMPORARY TABLE admin_users AS " +
           "SELECT u.id FROM users u " +
           "JOIN user_roles ur ON u.id = ur.user_id " +
           "JOIN roles r ON ur.role_id = r.id " +
           "WHERE r.name = 'ROLE_ADMIN';" +
           
           // Delete data from tables with no dependencies first
           "DELETE FROM google_drive_file;" +
           "ALTER TABLE google_drive_file AUTO_INCREMENT = 1;" +
           
           "DELETE FROM file;" +
           "ALTER TABLE file AUTO_INCREMENT = 1;" +
           
           "DELETE FROM lead_action;" +
           "ALTER TABLE lead_action AUTO_INCREMENT = 1;" +
           
           // Delete from tables with dependencies
           "DELETE FROM trigger_contract;" +
           "ALTER TABLE trigger_contract AUTO_INCREMENT = 1;" +
           
           "DELETE FROM trigger_ticket;" +
           "ALTER TABLE trigger_ticket AUTO_INCREMENT = 1;" +
           
           "DELETE FROM trigger_lead;" +
           "ALTER TABLE trigger_lead AUTO_INCREMENT = 1;" +
           
           "DELETE FROM contract_settings;" +
           "ALTER TABLE contract_settings AUTO_INCREMENT = 1;" +
           
           "DELETE FROM ticket_settings;" +
           "ALTER TABLE ticket_settings AUTO_INCREMENT = 1;" +
           
           "DELETE FROM lead_settings;" +
           "ALTER TABLE lead_settings AUTO_INCREMENT = 1;" +
           
           // Delete user-related data except admin
           "DELETE FROM user_profile WHERE user_id NOT IN (SELECT id FROM admin_users);" +
           "ALTER TABLE user_profile AUTO_INCREMENT = 1;" +
           
           "DELETE FROM oauth_users WHERE user_id NOT IN (SELECT id FROM admin_users);" +
           "ALTER TABLE oauth_users AUTO_INCREMENT = 1;" +
           
           "DELETE FROM user_roles WHERE user_id NOT IN (SELECT id FROM admin_users);" +
           
           "DELETE FROM email_template WHERE user_id NOT IN (SELECT id FROM admin_users);" +
           "ALTER TABLE email_template AUTO_INCREMENT = 1;" +
           
           // Handle customer tables
           "DELETE FROM customer;" +
           "ALTER TABLE customer AUTO_INCREMENT = 1;" +
           
           "DELETE FROM customer_login_info;" +
           "ALTER TABLE customer_login_info AUTO_INCREMENT = 1;" +
           
           // Delete users except admin
           "DELETE FROM users WHERE id NOT IN (SELECT id FROM admin_users);" +
           // Don't reset users AUTO_INCREMENT to preserve admin IDs
           
           "DROP TEMPORARY TABLE IF EXISTS admin_users;" +
           "SET FOREIGN_KEY_CHECKS = 1;")
    int resetData();
}