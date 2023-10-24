package art.pricetracker.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usr")
@Getter
@NoArgsConstructor(force = true)
public class User {
    @Id
    private final String id;
    private final String name;
    private final String email;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy="user")
    @JsonManagedReference
    private List<TrackedProduct> trackedProducts;

    public User(GoogleUserInfo userInfo) {
        this.id = userInfo.getId();
        this.name = userInfo.getName();
        this.email = userInfo.getEmail();
        trackedProducts = new ArrayList<>();
    }
}