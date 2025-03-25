package site.easy.to.build.crm.util.data.importer;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerLoginInfo;

@Getter
@Setter
public class CustomerDataImport extends DataImport{

    private String email;
    private String name;

    @Override
    public void checkForeignKey() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void checkIntegrity() {
        
    }


    @Override
    public List<String> getValidStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    /* Missing values generated */
    private String generatePosition() {
        String[] positions = {
            "Manager", "Assistant", "Coordinator", "Specialist", "Director", 
            "Executive", "Consultant", "Analyst", "Representative"
        };
        return positions[(int)(Math.random() * positions.length)];
    }

    private String generatePhoneNumber() {
        return String.format("+1-%03d-%03d-%04d", 
            (int)(Math.random() * 1000),
            (int)(Math.random() * 1000),
            (int)(Math.random() * 10000)
        );
    }

    private String generateAddress() {
        return String.format("%d %s %s", 
            (int)(Math.random() * 9999),
            getRandomStreetName(),
            getRandomStreetType()
        );
    }

    private String getRandomStreetName() {
        String[] names = {
            "Maple", "Oak", "Pine", "Cedar", "Elm", "Washington", "Lincoln", 
            "Park", "River", "Mountain"
        };
        return names[(int)(Math.random() * names.length)];
    }

    private String getRandomStreetType() {
        String[] types = {
            "Street", "Avenue", "Road", "Lane", "Drive", "Boulevard", "Way"
        };
        return types[(int)(Math.random() * types.length)];
    }

    private String generateCity() {
        String[] cities = {
            "New York", "Los Angeles", "Chicago", "Houston", "Phoenix", 
            "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"
        };
        return cities[(int)(Math.random() * cities.length)];
    }

    private String generateState() {
        String[] states = {
            "CA", "NY", "TX", "FL", "IL", "PA", "OH", "GA", "NC", "MI"
        };
        return states[(int)(Math.random() * states.length)];
    }

    private String generateCountry() {
        return "United States"; // Default country
    }

    private String generateDescription() {
        return "Imported customer from CSV";
    }

    private String generateSocialMediaHandle(String platform) {
        return String.format("%s_%s_%d", 
            name.replaceAll("\\s+", "_").toLowerCase(), 
            platform,
            (int)(Math.random() * 1000)
        );
    }

    private CustomerLoginInfo generateCustomerLoginInfo(Customer customer) {
        CustomerLoginInfo loginInfo = new CustomerLoginInfo();
        loginInfo.setUsername(customer.getEmail());
        String tempPassword = UUID.randomUUID().toString().substring(0, 12);
        loginInfo.setPassword(tempPassword);
        loginInfo.setToken(UUID.randomUUID().toString());
        loginInfo.setPasswordSet(false);
        loginInfo.setCustomer(customer);
        return loginInfo;
    }
    
}
