package jwt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
  private UserDetailsService userDetailsService;
  private JwtUtil jwtUtil;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    // Логика аутентификации с использованием токена
    String token = (String) authentication.getCredentials();

    try {
      String username = jwtUtil.getUsernameFromToken(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    } catch (Exception e) {
      throw new BadCredentialsException("Invalid JWT Token");
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
