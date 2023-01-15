package com.cultegroup.findguide.data.model.location;

import com.cultegroup.findguide.data.utils.IdParent;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "countries")
public class Country implements IdParent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cointry_name")
    private String countryName;
    @Column(name = "cointry_initials")
    private String countryInitials;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<City> cities;

    public Country() {

    }

    public Country(String countryName, String countryInitials, List<City> cities) {
        this.countryName = countryName;
        this.countryInitials = countryInitials;
        this.cities = cities;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryInitials() {
        return countryInitials;
    }

    public void setCountryInitials(String countryInitials) {
        this.countryInitials = countryInitials;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}
