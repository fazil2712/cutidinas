package com.ppip.cutidinas.security;

import com.ppip.cutidinas.model.User;
import com.ppip.cutidinas.repository.UserRepository;
import com.ppip.cutidinas.service.CutiService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final CutiService cutiService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String username = authentication.getName(); // Usually badgeid or name depending on UserDetailsService
        userRepository.findByBadgeidOrName(username, username).ifPresent(user -> {
            cutiService.generateCutiIfNeeded(user);
        });

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
