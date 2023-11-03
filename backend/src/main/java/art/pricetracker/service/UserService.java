package art.pricetracker.service;

import art.pricetracker.dto.UserJson;
import art.pricetracker.model.User;
import art.pricetracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerNewUserAccount(UserJson userJson) {
        // TODO: 2021-10-13 00:00:00.000000
        //  Check if user already exists
        var user = new User(userJson);

        log.warn("User: " + userJson.getName() + " " + userJson.getPassword());
        log.warn("Password encoded: " + passwordEncoder.encode(userJson.getPassword()));

        user.setPassword(passwordEncoder.encode(userJson.getPassword()));
        return userRepository.save(user);
    }

    public User getByName(String name) {
        return userRepository.findByName(name).orElseThrow(() -> new RuntimeException("User not found"));
    }

}