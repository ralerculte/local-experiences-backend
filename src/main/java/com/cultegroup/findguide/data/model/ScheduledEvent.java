package com.cultegroup.findguide.data.model;

import com.cultegroup.findguide.data.utils.IdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "scheduled_events")
public class ScheduledEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    @Column(name = "start_hour")
    private int startHour;
    @Column(name = "end_hour")
    private int endHour;
    @Column(name = "occupied_places")
    private int occupiedPlaces;

    @ManyToOne
    @JoinColumn(name = "experience_id")
    @JsonSerialize(using = IdSerializer.class)
    Experience experience;

    public ScheduledEvent() {
    }

    public ScheduledEvent(LocalDate date, int startHour, int endHour, int occupiedPlaces, Experience experience) {
        this.date = date;
        this.startHour = startHour;
        this.endHour = endHour;
        this.occupiedPlaces = occupiedPlaces;
        this.experience = experience;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getOccupiedPlaces() {
        return occupiedPlaces;
    }

    public void setOccupiedPlaces(int occupiedPlaces) {
        this.occupiedPlaces = occupiedPlaces;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }
}
