package art.pricetracker.controller;

import art.pricetracker.entity.user.UserJson;
import art.pricetracker.entity.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/authenticated")
    public ResponseEntity<?> isAuthenticated(Principal principal) {
        // TODO: 2021-10-13 00:00:00.000000
        // Change return type to ResponseEntity

        log.warn("Entered isAuthenticated method");
        return principal != null ? ResponseEntity.ok(HttpStatus.OK) : ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response) {
        log.warn("Entered login method");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        log.warn("Entered logout method");

        SecurityContextHolder.clearContext();
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.ok(HttpStatus.OK);
    }
}