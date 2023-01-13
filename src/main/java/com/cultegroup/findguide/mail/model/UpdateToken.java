package com.cultegroup.findguide.mail.model;

import com.cultegroup.findguide.data.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "update_token")
public class UpdateToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UpdateToken() {
    }

    public UpdateToken(String token, User user, LocalDateTime dateExpiration) {
        this.token = token;
        this.user = user;
        this.dateExpiration = dateExpiration;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
}
