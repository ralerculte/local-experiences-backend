package com.cultegroup.localexperiences.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Schema(name = "Таблица описывающая пользователей.")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "Уникальный идентификатор.")
    private Long id;
    @Schema(name = "Имя пользователя.")
    private String name;
    @Schema(name = "Фамилия пользователя.")
    private String surname;
    @Schema(name = "Электронная почта пользователя.")
    private String email;
    @Column(name = "phone_number")
    @Schema(name = "Номер телефона пользователя.")
    private String phoneNumber;
    @Enumerated
    @Schema(name = "Статус, показывающий активирован ли аккаунт.")
    private Status status;

    @JsonIgnore
    @Schema(name = "Пароль пользователя.")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(name = "Набор впечатлений, организатором которых является пользователь.")
    private Set<Experience> experiences = new HashSet<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(name = "Набор ивентов, в которых участвовал пользователь.")
    private Set<Event> events = new HashSet<>();

    public User() {
    }

    public User(String name,
                String surname,
                String email,
                String phoneNumber,
                Status status,
                String password,
                Set<Experience> experiences,
                Set<Event> events) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.password = password;
        this.experiences = experiences;
        this.events = events;
    }

    public User(String name, String surname, String email, String phoneNumber, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;

        this.status = Status.INACTIVE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(Set<Experience> experiences) {
        this.experiences = experiences;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public void addExperience(Experience experience) {
        if (experiences == null) {
            experiences = new HashSet<>();
        }
        experience.setUser(this);
        experiences.add(experience);
    }

    public void addEvent(Event event) {
        if (events == null) {
            events = new HashSet<>();
        }
        event.setUser(this);
        events.add(event);
    }
}