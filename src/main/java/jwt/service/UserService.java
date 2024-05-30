package jwt.service;

import jwt.controller.Controller;
import jwt.security.SecurityConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jwt.dto.TokenDto;
import jwt.dto.UserDto;
import jwt.model.Role;
import jwt.model.User;
import jwt.repository.UserRepository;
import jwt.security.JwtUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private UserRepository userRepository;
  private RoleService roleService;
  private final JwtUtil jwtUtil;
  private static final Logger log = LoggerFactory.getLogger(Controller.class);
  private SecurityConfig securityConfig;

  @Autowired
  public UserService(UserRepository userRepository, RoleService roleService, JwtUtil jwtUtil,
      SecurityConfig securityConfig) {
    this.userRepository = userRepository;
    this.roleService = roleService;
    this.jwtUtil = jwtUtil;
    this.securityConfig = securityConfig;
  }

  public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
    try {
      User user = findByName(name).orElseThrow(() -> new UsernameNotFoundException("Пользователь " + name + " не найден"));
      if (user.getLocked() <= 0) {
        throw new BadCredentialsException("Пользователь " + name + " заблокирован");
      }
      return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(),
          user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()));
    } catch (Exception e) {
      log.info(e.getMessage());
      throw e;
    }
  }

  public Optional<User> findByName(String name) {
    return userRepository.findByName(name);
  }

  public void createNewUser(UserDto userDto) {
    List<Role> roles = new ArrayList<>(List.of());
    roles.add(roleService.getUserRole().get());
    User user = new User();
    user.setName(userDto.getName());
    user.setPassword(securityConfig.passwordEncoder().encode(userDto.getPassword()));
    user.setRoles(roles);
    userRepository.save(user);
  }

  public void lockedUser(String name) {
    User user = findByName(name).get();
    if (user.getLocked() > 0) {
      user.setLocked(user.getLocked() - 1);
      userRepository.save(user);
    }
  }

  public ResponseEntity<?> authenticateUser(UserDto userDto) {
    UserDetails userDetails = loadUserByUsername(userDto.getName());
    if (!securityConfig.passwordEncoder().matches(userDto.getPassword(), userDetails.getPassword())) {
      log.info("Неверный пароль. User: {}", userDto.getName());
      lockedUser(userDto.getName());
      throw new BadCredentialsException("Неверный пароль");
    }
    String token = jwtUtil.generateToken(userDetails);
    return ResponseEntity.ok(new TokenDto(token));
  }
}