package com.cultegroup.findguide.data.model.location;

import com.cultegroup.findguide.data.model.Experience;
import com.cultegroup.findguide.data.utils.EntityId;
import com.cultegroup.findguide.data.utils.IdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "locations")
public class Location implements EntityId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String address;

    @OneToMany(mappedBy = "location")
    private Set<Experience> experiences = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "city_id")
    @JsonSerialize(using = IdSerializer.class)
    private City city;

    public Location() {
    }

    public Location(String address, City city, Set<Experience> experiences) {
        this.address = address;
        this.city = city;
        this.experiences = experiences;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Set<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(Set<Experience> experiences) {
        this.experiences = experiences;
    }
}
