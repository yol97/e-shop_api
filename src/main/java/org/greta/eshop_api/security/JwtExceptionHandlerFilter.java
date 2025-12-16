package org.greta.eshop_api.security;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.greta.eshop_api.exceptions.JwtValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component // → rend ce filtre détectable par Spring, pour qu’il puisse être ajouté à la chaîne de sécurité (bean)
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // On laisse la requête suivre son cours normal
            filterChain.doFilter(request, response);

        } catch (JwtValidationException e) {
            handleJwtError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            handleJwtError(response, HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne d’authentification");
        }
    }

    private void handleJwtError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
