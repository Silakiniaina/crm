package site.easy.to.build.crm.util.data.importer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
public class CustomerDataImport extends DataImport {

    private String email;
    private String name;

    private CustomerRepository customerRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private CustomerLoginInfoRepository customerLoginInfoRepository;
    private User currentUser;

    @Autowired
    public CustomerDataImport(CustomerRepository customerRepository,
                            UserRepository userRepository,
                            RoleRepository roleRepository,
                            CustomerLoginInfoRepository customerLoginInfoRepository,
                            User currentUser) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerLoginInfoRepository = customerLoginInfoRepository;
        this.currentUser = currentUser;
    }

    @Override
    public void checkIntegrity() {
        this.checkRequiredValue(this.getEmail(), "customer_email");
        this.validateEmail(this.getEmail());
        this.checkRequiredValue(this.getName(), "customer_name");
    }

    @Override
    public void checkForeignKey() {
        if (customerRepository.findByEmail(this.getEmail()) != null) {
           this.getErrors().add("Customer with email " + this.getEmail() + " already exists at line " + this.getLineNumber());
           this.setValid(false);
        }
    }

    @Override
    public List<String> getValidStatus() {
        return null;
    }

    @Override
    public void insertData(){
        if (!isValid()) {
            return;
        }

        Customer customer = toCustomer();
        if (customer != null) {
            User generatedUser = customer.getUser();
            CustomerLoginInfo loginInfo = customer.getCustomerLoginInfo();

            if (generatedUser != null) {
                userRepository.save(generatedUser);
            }else{
                System.out.println("Generated user null");
            }
            if (loginInfo != null) {
                customerLoginInfoRepository.save(loginInfo);
            }
            customerRepository.save(customer);
        }
    }

    public Customer toCustomer() {
        if (!isValid()) {
            return null;
        }

        Customer customer = new Customer();
        customer.setEmail(this.getEmail());
        customer.setName(this.getName());
        customer.setUser(this.getCurrentUser());
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

    private String generatePosition() {
        String[] positions = {
            "Manager", "Assistant", "Coordinator", "Specialist", "Director",
            "Executive", "Consultant", "Analyst", "Representative"
        };
        return positions[new Random().nextInt(positions.length)];
    }

    private String generatePhoneNumber() {
        return String.format("+1-%03d-%03d-%04d",
            new Random().nextInt(1000),
            new Random().nextInt(1000),
            new Random().nextInt(10000)
        );
    }

    private String generateAddress() {
        return String.format("%d %s %s",
            new Random().nextInt(9999),
            getRandomStreetName(),
            getRandomStreetType()
        );
    }

    private String getRandomStreetName() {
        String[] names = {
            "Maple", "Oak", "Pine", "Cedar", "Elm", "Washington", "Lincoln",
            "Park", "River", "Mountain"
        };
        return names[new Random().nextInt(names.length)];
    }

    private String getRandomStreetType() {
        String[] types = {
            "Street", "Avenue", "Road", "Lane", "Drive", "Boulevard", "Way"
        };
        return types[new Random().nextInt(types.length)];
    }

    private String generateCity() {
        String[] cities = {
            "New York", "Los Angeles", "Chicago", "Houston", "Phoenix",
            "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"
        };
        return cities[new Random().nextInt(cities.length)];
    }

    private String generateState() {
        String[] states = {
            "CA", "NY", "TX", "FL", "IL", "PA", "OH", "GA", "NC", "MI"
        };
        return states[new Random().nextInt(states.length)];
    }

    private String generateCountry() {
        return "United States";
    }

    private String generateDescription() {
        return "Imported customer from CSV by " + currentUser.getUsername();
    }

    private String generateSocialMediaHandle(String platform) {
        return String.format("%s_%s_%d",
            name.replaceAll("\\s+", "_").toLowerCase(),
            platform,
            new Random().nextInt(1000)
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
        user.setStatus("inactive");
        user.setCreatedAt(LocalDateTime.now());
        user.setHireDate(LocalDate.now());
        user.setToken(UUID.randomUUID().toString());
        user.setPasswordSet(false);

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER");
        List<Role> roles = new ArrayList<>();
        roles.add(customerRole);
        user.setRoles(roles);

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CustomerDataImport{\n");
        sb.append("  email='").append(email != null ? email : "null").append("',\n");
        sb.append("  name='").append(name != null ? name : "null").append("',\n");
        sb.append("  errors=").append(this.getErrors() != null ? this.getErrors().toString() : "null").append(",\n");
        sb.append("  lineNumber=").append(this.getLineNumber()).append("\n");

        Customer customer = toCustomer();
        if (customer != null) {
            sb.append("  generatedCustomer={\n");
            sb.append("    email='").append(customer.getEmail()).append("',\n");
            sb.append("    name='").append(customer.getName()).append("',\n");
            sb.append("    position='").append(customer.getPosition()).append("',\n");
            sb.append("    phone='").append(customer.getPhone()).append("',\n");
            sb.append("    address='").append(customer.getAddress()).append("',\n");
            sb.append("    city='").append(customer.getCity()).append("',\n");
            sb.append("    state='").append(customer.getState()).append("',\n");
            sb.append("    country='").append(customer.getCountry()).append("',\n");
            sb.append("    description='").append(customer.getDescription()).append("',\n");
            sb.append("    twitter='").append(customer.getTwitter()).append("',\n");
            sb.append("    facebook='").append(customer.getFacebook()).append("',\n");
            sb.append("    youtube='").append(customer.getYoutube()).append("',\n");
            sb.append("    createdAt=").append(customer.getCreatedAt()).append("\n");
            sb.append("  }\n");
        }
        sb.append("}");
        return sb.toString();
    }
}