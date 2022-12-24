package com.cultegroup.localexperiences.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "events")
@Schema(description = """
        Таблица, описывающая ивенты.
        Ивент представляет из себя предстоящее/прошедшее впечатление для пользователя, который не является организатором.
        """)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "Уникальный идентификатор.")
    private Long id;
    @Column(name = "local_rating")
    @Schema(name = "Рейтинг для события, выставленный пользователем-владельцем ивента.")
    private Integer localRating;
    @Schema(name = "Дата проведения ивента.")
    private LocalDate date;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "experience_id")
    @Schema(name = "Родительское впечатление.")
    private Experience experience;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(name = "Пользователь, который является владельцем ивента.")
    private User user;

    public Event() {
    }

    public Event(Integer localRating, LocalDate date, Experience experience) {
        this.localRating = localRating;
        this.date = date;
        this.experience = experience;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Integer getLocalRating() {
        return localRating;
    }

    public void setLocalRating(Integer localRating) {
        this.localRating = localRating;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
