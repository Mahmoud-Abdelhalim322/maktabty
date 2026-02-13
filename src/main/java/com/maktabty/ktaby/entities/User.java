package com.maktabty.ktaby.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    private String password;
    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_favorites",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "book_id"))
    private Set<Book> favorites = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public Long getId()
     { return id; }

    public void setId(Long id)
     { this.id = id; }

    public String getUsername()
     { return username; }

    public void setUsername(String username)
     { this.username = username; }
     
    public String getPassword()
     { return password; }

    public void setPassword(String password)
     { this.password = password; }

    public String getEmail()
     { return email; }

    public void setEmail(String email)
     { this.email = email; }

    public Set<Role> getRoles()
     { return roles; }

    public void setRoles(Set<Role> roles)
     { this.roles = roles; }

    public Set<Book> getFavorites()
     { return favorites; }

    public void setFavorites(Set<Book> favorites)
     { this.favorites = favorites; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", favorites=" + favorites +
                '}';
    }
}
