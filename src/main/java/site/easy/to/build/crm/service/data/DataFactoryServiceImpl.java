package site.easy.to.build.crm.service.data; // Adjust package as needed

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataFactoryServiceImpl implements DataFactoryService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public int resetData() {
        // Disable foreign key checks to allow deletion without constraint violations
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        // Truncate tables that don’t directly depend on keeping the admin user
        entityManager.createNativeQuery("TRUNCATE TABLE employee").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE email_template").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE customer_login_info").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE customer").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE trigger_lead").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE trigger_ticket").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE trigger_contract").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE contract_settings").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE lead_action").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE lead_settings").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE ticket_settings").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE file").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE google_drive_file").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE customer_budget").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE expenses").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE budget_alert_threshold").executeUpdate();

        // Delete from dependent tables linked to users (except admin)
        entityManager.createNativeQuery(
            "DELETE FROM oauth_users WHERE user_id != (SELECT user_id FROM user_roles WHERE role_id = 1 LIMIT 1)"
        ).executeUpdate();
        entityManager.createNativeQuery(
            "DELETE FROM user_profile WHERE user_id != (SELECT user_id FROM user_roles WHERE role_id = 1 LIMIT 1)"
        ).executeUpdate();

        // Delete user_roles for all users except the admin (ROLE_MANAGER, role_id = 1)
        entityManager.createNativeQuery(
            "DELETE FROM user_roles WHERE user_id NOT IN " +
            "(SELECT ur.user_id FROM (SELECT user_id FROM user_roles WHERE role_id = 1 LIMIT 1) AS ur)"
        ).executeUpdate();

        // Delete all users except the admin (identified by ROLE_MANAGER)
        entityManager.createNativeQuery(
            "DELETE FROM users WHERE id != (SELECT user_id FROM user_roles WHERE role_id = 1 LIMIT 1)"
        ).executeUpdate();

        // Reset auto-increment values to their configured initial values
        entityManager.createNativeQuery("ALTER TABLE employee AUTO_INCREMENT = 9").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE email_template AUTO_INCREMENT = 35").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE customer_login_info AUTO_INCREMENT = 19").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE customer AUTO_INCREMENT = 43").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE trigger_lead AUTO_INCREMENT = 56").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE trigger_ticket AUTO_INCREMENT = 47").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE trigger_contract AUTO_INCREMENT = 19").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE contract_settings AUTO_INCREMENT = 4").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE lead_action AUTO_INCREMENT = 13").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE lead_settings AUTO_INCREMENT = 3").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE ticket_settings AUTO_INCREMENT = 6").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE file AUTO_INCREMENT = 140").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE google_drive_file AUTO_INCREMENT = 52").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE customer_budget AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE expenses AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE budget_alert_threshold AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE users AUTO_INCREMENT = 52").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE oauth_users AUTO_INCREMENT = 28").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE user_profile AUTO_INCREMENT = 33").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE roles AUTO_INCREMENT = 4").executeUpdate();

        // Re-enable foreign key checks
        return entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}