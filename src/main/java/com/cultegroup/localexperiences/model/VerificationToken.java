package com.cultegroup.localexperiences.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;

@Entity
@Table(name = "verification_tokens")
@Schema(name = "Таблица, описывающая токен верификации.")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "Уникальный идентификатор.")
    private Long id;
    @Schema(name = "UUID для идентификации пользователя.")
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    @Schema(name = "Пользователь, которому принадлежит токен верификации.")
    private User user;

    public VerificationToken() {

    }

    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
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
}
