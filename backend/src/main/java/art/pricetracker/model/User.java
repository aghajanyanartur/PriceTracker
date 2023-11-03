package art.pricetracker.model;

import art.pricetracker.dto.UserJson;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usr")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String password;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy="user")
    @JsonManagedReference
    private List<TrackedProduct> trackedProducts;

    // TODO: 2021-10-13 00:00:00.000000
    //  This constructor is only for testing purposes.
    public User() {
        this.name = "hardcoded user name";
        this.password = "hardcoded email";
        trackedProducts = new ArrayList<>();
    }

    public User(UserJson userJson) {
        this.name = userJson.getName();
        trackedProducts = new ArrayList<>();
    }
}