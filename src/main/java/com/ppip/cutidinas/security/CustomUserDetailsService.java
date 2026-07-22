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
        // Find by badgeid or name
        User user = userRepository.findByBadgeidOrName(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with badge ID or name: " + username));

        List<SimpleGrantedAuthority> authorities = user.getPermissions().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new CustomUserDetails(
                user,
                authorities
        );
    }
}
