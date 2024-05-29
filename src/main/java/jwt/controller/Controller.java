package jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jwt.dto.UserDto;
import jwt.service.UserService;

import java.security.Principal;

@RestController
public class Controller {
  private final UserService userService;

  public Controller(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/user")
  public String securedData() {
    return "User data";
  }

  @GetMapping("/admin")
  public String adminData() {
    return "Admin data";
  }

  @GetMapping("/info")
  public String userData(Principal principal) {
    return principal.getName();
  }

  @PostMapping("/auth")
  public ResponseEntity<?> createAuthenticationToken(@RequestBody UserDto userDto) {
    return userService.authenticateUser(userDto);
  }

  @PostMapping("/registration")
  public void createNewUser(@RequestBody UserDto userDto) {
    userService.createNewUser(userDto);
  }
}
