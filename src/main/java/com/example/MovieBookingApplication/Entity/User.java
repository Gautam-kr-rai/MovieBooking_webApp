package com.example.MovieBookingApplication.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Entity
@Data
@Table(name= "users")
public class User  implements UserDetails {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    @Getter
    private String email;
    @Getter
    private String password;

     @Getter
     @ElementCollection(fetch = FetchType.EAGER)
     private Set<String> role;

    @Getter
    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());


    }

    @Override
    public String getUsername() {
        return username;
    }
}
