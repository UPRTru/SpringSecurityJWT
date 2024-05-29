package jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  @Autowired
  public UserService(UserRepository userRepository, RoleService roleService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.roleService = roleService;
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
  }

  public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
    User user = findByName(name).orElseThrow(() -> new UsernameNotFoundException(
        String.format("Пользователь '%s' не найден", name)
    ));
    return new org.springframework.security.core.userdetails.User(
        user.getName(),
        user.getPassword(),
        user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
    );
  }

  public Optional<User> findByName(String name) {
    return userRepository.findByName(name);
  }

  public void createNewUser(UserDto userDto) {
    List<Role> roles = new ArrayList<>(List.of());
    roles.add(roleService.getUserRole().get());
    User user = new User();
    user.setName(userDto.getName());
    user.setPassword(userDto.getPassword());
    user.setRoles(roles);
    userRepository.save(user);
  }

  public ResponseEntity<?> authenticateUser(UserDto userDto) {
    UserDetails userDetails = loadUserByUsername(userDto.getName());
    if (!userDetails.getPassword().equals(userDto.getPassword())) {
      throw new BadCredentialsException("Неверный пароль");
    }
    String token = jwtUtil.generateToken(userDetails);
    return ResponseEntity.ok(new TokenDto(token));
  }
}