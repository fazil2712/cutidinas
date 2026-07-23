package com.ppip.cutidinas.security;

import com.ppip.cutidinas.model.User;
import com.ppip.cutidinas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails.setUserRepository(userRepository);
        // Find by badgeid or name
        User user = userRepository.findByBadgeidOrName(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with badge ID or name: " + username));

        java.util.Set<String> permSet = new java.util.HashSet<>(user.getPermissions());
        if (permSet.contains("ROLE_ADMIN")) {
            permSet.add("PERM_ADMIN_PANEL");
            permSet.add("PERM_PERSETUJUAN_CUTI");
            permSet.add("PERM_KELOLA_PENGAJUAN");
            permSet.add("PERM_KELOLA_HARI_LIBUR");
        }

        List<SimpleGrantedAuthority> authorities = permSet.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new CustomUserDetails(
                user,
                authorities
        );
    }
}
