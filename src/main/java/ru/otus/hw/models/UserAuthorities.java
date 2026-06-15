package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "user_authorities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthorities {

    @EmbeddedId
    private UserAuthorityPk userAuthorityPk = new UserAuthorityPk();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usernameId")
    @JoinColumn(name = "username_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "authority", insertable = false, updatable = false)
    private String authority;

    public void setUser(User user) {
        this.user = user;
        this.userAuthorityPk.usernameId = user.getUsername();
    }

    public UserAuthorities(User user, String authority) {
        this.user = user;
        this.userAuthorityPk.usernameId = user.getUsername();
        this.userAuthorityPk.authority = authority;
    }

    @Embeddable
    @Data
    public static class UserAuthorityPk implements Serializable {

        @Column(name = "username_id")
        private String usernameId;

        @Column(name = "authority")
        private String authority;
    }
}
