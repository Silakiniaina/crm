package site.easy.to.build.crm.util.data.importer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerLoginInfo;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.entity.UserProfile;

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
    
    private User generateUser() {
        User user = new User();
        user.setUsername(this.getName());
        user.setEmail(this.getEmail());
        user.setPassword(UUID.randomUUID().toString().substring(0, 12)); 
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setHireDate(LocalDate.now());
        user.setToken(UUID.randomUUID().toString());
        user.setPasswordSet(false);
        UserProfile userProfile = generateUserProfile(user);
        user.setUserProfile(userProfile);
        return user;
    }

    private UserProfile generateUserProfile(User user) {
        UserProfile profile = new UserProfile();
        String[] nameParts = name.trim().split("\\s+");
        profile.setFirstName(nameParts[0]);
        profile.setLastName(nameParts.length > 1 ? nameParts[1] : "Imported");
        profile.setCountry(generateCountry());
        profile.setPhone(generatePhoneNumber());
        profile.setPosition(generatePosition());
        profile.setDepartment("Customer Relations");
        profile.setStatus("active");
        profile.setFacebook(generateSocialMediaHandle("facebook"));
        profile.setTwitter(generateSocialMediaHandle("twitter"));
        profile.setYoutube(generateSocialMediaHandle("youtube"));
        profile.setBio("Generated user profile for imported customer");
        profile.setAddress(generateAddress());
        profile.setUser(user);
        
        return profile;
    }

    public Customer toCustomer() {
        Customer customer = new Customer();
        customer.setEmail(this.getEmail());
        customer.setName(this.getName());
        User generatedUser = generateUser();
        if (generatedUser == null) {
            this.getErrors().add("Failed to generate user for customer import");
            return null;
        }
        customer.setUser(generatedUser);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setPosition(generatePosition());
        customer.setPhone(generatePhoneNumber());
        customer.setAddress(generateAddress());
        customer.setCity(generateCity());
        customer.setState(generateState());
        customer.setCountry(generateCountry());
        customer.setDescription(generateDescription());
        customer.setTwitter(generateSocialMediaHandle("twitter"));
        customer.setFacebook(generateSocialMediaHandle("facebook"));
        customer.setYoutube(generateSocialMediaHandle("youtube"));
        customer.setCustomerLoginInfo(generateCustomerLoginInfo(customer));

        return customer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CustomerDataImport{\n");

        // Basic fields from this class
        sb.append("  email='").append(email != null ? email : "null").append("',\n");
        sb.append("  name='").append(name != null ? name : "null").append("',\n");

        // Fields inherited from DataImport (assuming these exist based on context)
        sb.append("  email='").append(email != null ? email : "null").append("',\n");
        sb.append("  name='").append(name != null ? name : "null").append("',\n");
        sb.append("  errors=").append(this.getErrors() != null ? this.getErrors().toString() : "null").append(",\n");
        //sb.append("  processedEmails=").append(processedEmails != null ? processedEmails.toString() : "null").append(",\n");

        // Convert the object to Customer and include its details if possible
        Customer customer = toCustomer();
        if (customer != null) {
            sb.append("  generatedCustomer={\n");
            sb.append("    email='").append(customer.getEmail() != null ? customer.getEmail() : "null").append("',\n");
            sb.append("    name='").append(customer.getName() != null ? customer.getName() : "null").append("',\n");
            sb.append("    position='").append(customer.getPosition() != null ? customer.getPosition() : "null").append("',\n");
            sb.append("    phone='").append(customer.getPhone() != null ? customer.getPhone() : "null").append("',\n");
            sb.append("    address='").append(customer.getAddress() != null ? customer.getAddress() : "null").append("',\n");
            sb.append("    city='").append(customer.getCity() != null ? customer.getCity() : "null").append("',\n");
            sb.append("    state='").append(customer.getState() != null ? customer.getState() : "null").append("',\n");
            sb.append("    country='").append(customer.getCountry() != null ? customer.getCountry() : "null").append("',\n");
            sb.append("    description='").append(customer.getDescription() != null ? customer.getDescription() : "null").append("',\n");
            sb.append("    twitter='").append(customer.getTwitter() != null ? customer.getTwitter() : "null").append("',\n");
            sb.append("    facebook='").append(customer.getFacebook() != null ? customer.getFacebook() : "null").append("',\n");
            sb.append("    youtube='").append(customer.getYoutube() != null ? customer.getYoutube() : "null").append("',\n");
            sb.append("    createdAt=").append(customer.getCreatedAt() != null ? customer.getCreatedAt().toString() : "null").append(",\n");

            // User details
            User user = customer.getUser();
            if (user != null) {
                sb.append("    user={\n");
                sb.append("      username='").append(user.getUsername() != null ? user.getUsername() : "null").append("',\n");
                sb.append("      email='").append(user.getEmail() != null ? user.getEmail() : "null").append("',\n");
                sb.append("      status='").append(user.getStatus() != null ? user.getStatus() : "null").append("',\n");
                sb.append("      hireDate=").append(user.getHireDate() != null ? user.getHireDate().toString() : "null").append(",\n");
                sb.append("      createdAt=").append(user.getCreatedAt() != null ? user.getCreatedAt().toString() : "null").append(",\n");

                // UserProfile details
                UserProfile profile = user.getUserProfile();
                if (profile != null) {
                    sb.append("      profile={\n");
                    sb.append("        firstName='").append(profile.getFirstName() != null ? profile.getFirstName() : "null").append("',\n");
                    sb.append("        lastName='").append(profile.getLastName() != null ? profile.getLastName() : "null").append("',\n");
                    sb.append("        country='").append(profile.getCountry() != null ? profile.getCountry() : "null").append("',\n");
                    sb.append("        phone='").append(profile.getPhone() != null ? profile.getPhone() : "null").append("',\n");
                    sb.append("        position='").append(profile.getPosition() != null ? profile.getPosition() : "null").append("',\n");
                    sb.append("        department='").append(profile.getDepartment() != null ? profile.getDepartment() : "null").append("',\n");
                    sb.append("        status='").append(profile.getStatus() != null ? profile.getStatus() : "null").append("',\n");
                    sb.append("        bio='").append(profile.getBio() != null ? profile.getBio() : "null").append("'\n");
                    sb.append("      },\n");
                }
                sb.append("    },\n");
            }

            // CustomerLoginInfo details
            CustomerLoginInfo loginInfo = customer.getCustomerLoginInfo();
            if (loginInfo != null) {
                sb.append("    loginInfo={\n");
                sb.append("      username='").append(loginInfo.getUsername() != null ? loginInfo.getUsername() : "null").append("',\n");
                sb.append("      token='").append(loginInfo.getToken() != null ? loginInfo.getToken() : "null").append("',\n");
                sb.append("      passwordSet=").append(loginInfo.isPasswordSet()).append("\n");
                sb.append("    }\n");
            }
            sb.append("  }\n");
        } else {
            sb.append("  generatedCustomer=null (validation failed or incomplete data)\n");
        }

        sb.append("}");
        return sb.toString();
    }
}
