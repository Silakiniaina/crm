package site.easy.to.build.crm.service.data;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class DataFactoryServiceImpl implements DataFactoryService{

@PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public int resetData() {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate(); 

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

        return entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}
