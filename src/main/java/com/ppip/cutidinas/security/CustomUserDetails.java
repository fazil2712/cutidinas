package com.ppip.cutidinas.security;

import com.ppip.cutidinas.model.User;
import com.ppip.cutidinas.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Collection<? extends GrantedAuthority> initialAuthorities;
    private static UserRepository userRepository;

    public static void setUserRepository(UserRepository repo) {
        userRepository = repo;
    }

    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.initialAuthorities = authorities;
    }

    public String getFullName() {
        if (userRepository != null) {
            return userRepository.findById(user.getBadgeid()).map(User::getName).orElse(user.getName());
        }
        return user.getName();
    }
    
    public String getBadgeId() {
        return user.getBadgeid();
    }
    
    public String getRoleName() {
        if (getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "ADMIN";
        }
        return "USER";
    }

    public String getFotoProfil() {
        if (userRepository != null) {
            return userRepository.findById(user.getBadgeid()).map(User::getProfilePicture).orElse(user.getProfilePicture());
        }
        return user.getProfilePicture();
    }

    public User getUser() {
        if (userRepository != null) {
            return userRepository.findById(user.getBadgeid()).orElse(user);
        }
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userRepository != null) {
            User freshUser = userRepository.findById(user.getBadgeid()).orElse(user);
            if (freshUser != null && freshUser.getPermissions() != null) {
                Set<String> permSet = new HashSet<>(freshUser.getPermissions());
                if (permSet.contains("ROLE_ADMIN")) {
                    permSet.add("PERM_ADMIN_PANEL");
                    permSet.add("PERM_PERSETUJUAN_CUTI");
                    permSet.add("PERM_KELOLA_PENGAJUAN");
                    permSet.add("PERM_KELOLA_HARI_LIBUR");
                }
                return permSet.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }
        }
        return initialAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getBadgeid();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
