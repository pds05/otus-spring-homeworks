package ru.otus.hw.models;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(name = "user-authorities-entity-graph",
        attributeNodes = @NamedAttributeNode("userAuthorities"))
public class User {

    @Id
    private String username;

    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserAuthorities> userAuthorities = new ArrayList<>();

    public void addAuthority(String authority) {
        UserAuthorities userAuthorities = new UserAuthorities(this, authority);
        this.userAuthorities.add(userAuthorities);
    }
}
