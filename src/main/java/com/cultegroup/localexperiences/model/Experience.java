package com.cultegroup.localexperiences.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;

@Entity
@Table(name = "experiences")
@Schema(name = """
        Таблица, описывающая впечатления.
        Впечатление - событие, которое создаётся организатором. Для всех остальных пользователей создаются конкретные ивенты.
        """)
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "Уникальный идентификатор.")
    private Long id;
    @Schema(name = "Название впечатления.")
    private String name;
    @Schema(name = "Место проведения.")
    private String location;
    @Schema(name = "Описание впечатления.")
    private String description;
    @Schema(name = "Общий рейтинг впечатления, который формируется на основе всех локальных рейтингов у дочерних ивентов.")
    private Double rating;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(name = "Пользователь-организатор впечатления.")
    private User user;

    public Experience() {
    }

    public Experience(String name, String location, String description, Double rating) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.rating = rating;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
