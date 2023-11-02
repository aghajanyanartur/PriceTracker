package art.pricetracker.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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


    // TODO: 2021-10-13 00:00:00.000000
    //  This constructor is only for testing purposes.
    public User() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.name = "hardcoded user name";
        this.email = "hardcoded email";
        trackedProducts = new ArrayList<>();
    }
}