package art.pricetracker.controller;

import art.pricetracker.dto.UserJson;
import art.pricetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping
    public void register(@RequestBody UserJson userJson) {
        //TODO: 2021-10-13 00:00:00.000000
        // Change return type to ResponseEntity

        userService.registerNewUserAccount(userJson);
    }
}
