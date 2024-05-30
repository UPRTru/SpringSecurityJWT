package jwt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private static final Logger log = LoggerFactory.getLogger(Controller.class);

  private final UserService userService;

  public Controller(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/user")
  public String securedData(Principal principal) {
    log.info("Get /user. User: {}", principal.getName());
    return "User data";
  }

  @GetMapping("/admin")
  public String adminData(Principal principal) {
    log.info("Get /admin. User: {}", principal.getName());
    return "Admin data";
  }

  @GetMapping("/info")
  public String userData(Principal principal) {
    log.info("Get /info. User: {}", principal.getName());
    return principal.getName();
  }

  @PostMapping("/auth")
  public ResponseEntity<?> createAuthenticationToken(@RequestBody UserDto userDto) {
    log.info("Post /auth. User: {}", userDto.getName());
    return userService.authenticateUser(userDto);
  }

  @PostMapping("/registration")
  public void createNewUser(@RequestBody UserDto userDto) {
    log.info("Post /registration. User: {}", userDto.getName());
    userService.createNewUser(userDto);
  }
}
