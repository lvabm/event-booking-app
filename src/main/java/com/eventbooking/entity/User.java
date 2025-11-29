package com.eventbooking.entity;

import com.eventbooking.common.base.BaseEntity;
import com.eventbooking.common.constant.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;


    @Column(nullable = false)
    private String password;


    private String avatar;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    // Quan hệ
    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Reminder reminder;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }


}
