package com.example.keycloakuserstore.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = "getUserByUsername", query = "select u from User u where u.username = :username"),
        @NamedQuery(name = "getUserByEmail", query = "select u from User u where u.email = :email"),
        @NamedQuery(name = "getUserCount", query = "select count(u) from User u"),
        @NamedQuery(name = "getAllUsers", query = "select u from User u"),
        @NamedQuery(name = "searchForUser", query = "select u from User u where " +
                "( lower(u.username) like :search or u.username like :search ) order by u.username"),
})
@Entity
@Table(name = "sso_user")
@Data
@Accessors(chain = true)
public class User {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private String id;

    @Column(unique = true, name = "username")
    private String username;

    private String password;

    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email",insertable = false,updatable = false)
    private String phone;

    @Column(name = "enabled")
    private boolean enabled;
}
