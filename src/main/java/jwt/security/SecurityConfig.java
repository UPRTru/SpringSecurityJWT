package jwt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
    return authConfiguration.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(cs -> cs.disable())
        .cors(co -> co.disable())
        .addFilterBefore(new jwt.security.JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .authenticationProvider(new jwt.security.JwtAuthenticationProvider())
        .authorizeHttpRequests(a -> a.requestMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/user").hasRole("USER")
            .requestMatchers(HttpMethod.GET, "/info").authenticated()
            .requestMatchers(HttpMethod.POST,"/auth").permitAll()
            .requestMatchers(HttpMethod.POST,"/registration").permitAll())
        .logout(l -> l.logoutSuccessUrl("/logout").permitAll())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
    return http.build();
  }
}
