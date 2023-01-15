package com.cultegroup.findguide.data.model.location;

import com.cultegroup.findguide.data.utils.IdParent;
import com.cultegroup.findguide.data.utils.IdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cities")
public class City implements IdParent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "city_name")
    private String cityName;

    @ManyToOne
    @JoinColumn(name = "country_id")
    @JsonSerialize(using = IdSerializer.class)
    private Country country;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> locations;

    public City() {
    }

    public City(String cityName, Country country, List<Location> locations) {
        this.cityName = cityName;
        this.country = country;
        this.locations = locations;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
