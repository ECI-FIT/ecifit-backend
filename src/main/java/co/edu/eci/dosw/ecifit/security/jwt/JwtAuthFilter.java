package co.edu.eci.dosw.ecifit.security.jwt;

import co.edu.eci.dosw.ecifit.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final ObjectProvider<JwtUtil> jwtUtilProvider;
    private final ObjectProvider<CustomUserDetailsService> userDetailsServiceProvider;

    public JwtAuthFilter(
            ObjectProvider<JwtUtil> jwtUtilProvider,
            ObjectProvider<CustomUserDetailsService> userDetailsServiceProvider
    ) {
        this.jwtUtilProvider = jwtUtilProvider;
        this.userDetailsServiceProvider = userDetailsServiceProvider;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        JwtUtil jwtUtil = jwtUtilProvider.getIfAvailable();
        CustomUserDetailsService customUserDetailsService = userDetailsServiceProvider.getIfAvailable();

        if (jwtUtil == null || customUserDetailsService == null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        try {
            final String email = jwtUtil.extractUsername(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Usuario autenticado exitosamente mediante JWT: {}", email);
                }
            }
        } catch (Exception e) {
            log.warn("No fue posible autenticar la petición con token JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
