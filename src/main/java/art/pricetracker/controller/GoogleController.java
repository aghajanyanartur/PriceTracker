package art.pricetracker.controller;

import art.pricetracker.model.GoogleUserInfo;
import art.pricetracker.model.User;
import art.pricetracker.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:3000")
public class GoogleController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public ResponseEntity<?> authorizedClient(OAuth2AuthenticationToken auth) {
        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        auth.getAuthorizedClientRegistrationId(),
                        auth.getName());

        OAuth2AccessToken accessToken =
                authorizedClient.getAccessToken();
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getTokenValue());
        HttpEntity entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GoogleUserInfo> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, GoogleUserInfo.class);

        GoogleUserInfo userInfo = response.getBody();
        User user = new User();
        user.setId(userInfo.getId());
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());

        userRepository.save(user);

        String redirectUrl = "http://localhost:3000/api/products";

        headers = new HttpHeaders();
        headers.add("Location", redirectUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/login-check")
    public ResponseEntity<Void> checkLoginStatus(HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            String redirectUrl = "http://localhost:3000/api/products";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", redirectUrl);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }
}