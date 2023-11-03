package art.pricetracker.controller;

import art.pricetracker.entity.user.UserJson;
import art.pricetracker.entity.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/")
@Slf4j
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public void register(@RequestBody UserJson userJson) {
        //TODO: 2021-10-13 00:00:00.000000
        // Change return type to ResponseEntity

        userService.registerNewUserAccount(userJson);
    }

    @GetMapping("/authenticated")
    public ResponseEntity<?> isAuthenticated(Principal principal) {
        log.warn("Entered isAuthenticated method,,,, Principal: " + principal);
        return principal != null ? ResponseEntity.ok(HttpStatus.OK) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
