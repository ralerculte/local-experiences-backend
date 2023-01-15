package com.cultegroup.findguide.data.model;

import com.cultegroup.findguide.data.model.enums.GuideOpinion;
import com.cultegroup.findguide.data.utils.EntityId;
import com.cultegroup.findguide.data.utils.IdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;

@Entity
@Table(name = "guides_info")
public class GuideInfo implements EntityId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "skills_description")
    private String skillsDescription;
    private GuideOpinion opinion;

    @OneToOne
    @JoinColumn(name = "experience_id")
    @JsonSerialize(using = IdSerializer.class)
    private Experience experience;

    public GuideInfo() {
    }

    public GuideInfo(String skillsDescription, GuideOpinion opinion, Experience experience) {
        this.skillsDescription = skillsDescription;
        this.opinion = opinion;
        this.experience = experience;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkillsDescription() {
        return skillsDescription;
    }

    public void setSkillsDescription(String skillsDescription) {
        this.skillsDescription = skillsDescription;
    }

    public GuideOpinion getOpinion() {
        return opinion;
    }

    public void setOpinion(GuideOpinion opinion) {
        this.opinion = opinion;
    }
}
