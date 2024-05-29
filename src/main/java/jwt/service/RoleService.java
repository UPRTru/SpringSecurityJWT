package jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jwt.model.Role;
import jwt.repository.RoleRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
  private final RoleRepository roleRepository;

  public Optional<Role> getUserRole() {
    return roleRepository.findByName("ROLE_USER");
  }
}
