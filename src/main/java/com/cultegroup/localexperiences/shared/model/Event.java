package com.cultegroup.localexperiences.shared.model;

import javax.persistence.*;

@Entity
@Table(name = "eventsV2")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer rating;
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "experience_id")
    private Experience experience;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Event() {
    }

    public Event(Integer rating, String feedback, Experience experience, User user) {
        this.rating = rating;
        this.feedback = feedback;
        this.experience = experience;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
