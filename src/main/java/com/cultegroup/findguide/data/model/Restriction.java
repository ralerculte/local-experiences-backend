package com.cultegroup.findguide.data.model;

import com.cultegroup.findguide.data.model.enums.ActivityLevel;
import com.cultegroup.findguide.data.model.enums.Preparation;
import com.cultegroup.findguide.data.utils.IdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;

@Entity
@Table(name = "restrictions")
public class Restriction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated
    @Column(name = "activity_level")
    private ActivityLevel activityLevel;
    @Enumerated
    private Preparation preparation;

    @OneToOne
    @JoinColumn(name = "experience_id")
    @JsonSerialize(using = IdSerializer.class)
    private Experience experience;

    public Restriction() {

    }

    public Restriction(ActivityLevel activityLevel, Preparation preparation, Experience experience) {
        this.activityLevel = activityLevel;
        this.preparation = preparation;
        this.experience = experience;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public Preparation getPreparation() {
        return preparation;
    }

    public void setPreparation(Preparation preparation) {
        this.preparation = preparation;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }
}
