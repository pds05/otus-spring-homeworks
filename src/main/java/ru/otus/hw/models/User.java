package ru.otus.hw.models;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(name = "user-role-entity-graph",
        attributeNodes = @NamedAttributeNode("userRoles"))
public class User {

    @Id
    private String username;

    private String password;

    @OneToMany(mappedBy = "username", fetch = FetchType.LAZY)
    private List<UserRole> userRoles;
}
