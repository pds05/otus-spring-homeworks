package ru.otus.hw.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.NamedSubgraph;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "userComments")
@EqualsAndHashCode(exclude = "userComments")

@Entity
@Table(name = "users")
@NamedEntityGraph(
        name = "user-entity-graph",
        attributeNodes = @NamedAttributeNode(value = "userComments",
                subgraph = "user-user-comments-entity-graph"),
        subgraphs = @NamedSubgraph(name = "user-user-comments-entity-graph",
                attributeNodes = {
                        @NamedAttributeNode("user"),
                        @NamedAttributeNode("book")})
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name")
    private String userName;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserComment> userComments;
}
