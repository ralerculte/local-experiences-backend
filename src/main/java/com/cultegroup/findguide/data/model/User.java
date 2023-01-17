package com.cultegroup.findguide.data.model;

import com.cultegroup.findguide.data.model.enums.Status;
import com.cultegroup.findguide.data.utils.EntityId;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usersV2")
public class User implements EntityId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    @JsonIgnore
    private String password;
    private String name;
    private String surname;
    private String location;
    @Column(name = "avatar_link")
    private String avatarLink;
    @Enumerated
    private Status status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Experience> experiences = new HashSet<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Event> events = new HashSet<>();

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(
            String email,
            String password,
            String name,
            String surname,
            String avatarLink,
            String location,
            Status status,
            Set<Experience> experiences,
            Set<Event> events
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.avatarLink = avatarLink;
        this.location = location;
        this.status = status;
        this.experiences = experiences;
        this.events = events;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
