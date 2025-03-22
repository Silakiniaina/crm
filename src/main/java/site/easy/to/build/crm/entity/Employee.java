package site.easy.to.build.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;

@Entity
@Table(name = "employee")
@NoArgsConstructor
@Getter
@Setter
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "Username is required")
    @Column(name = "username")
    private String username;
    
    @NotBlank(message = "First name is required")
    @Column(name = "first_name")
    private String first_name;
    
    @NotBlank(message = "Last name is required")
    @Column(name = "last_name")
    private String last_name;

    @NotBlank(message = "Email is required")
    @Column(name = "email")
    private String email; 
    
    @NotBlank(message = "Password is required")
    @Column(name = "password")
    private String password;

    @Column(name = "budget", nullable = false)
    private double budget;

    @Column(name = "provider")
    private String provider;

}
