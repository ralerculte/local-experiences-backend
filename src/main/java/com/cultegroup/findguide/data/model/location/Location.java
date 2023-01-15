package com.cultegroup.findguide.data.model.location;

import com.cultegroup.findguide.data.utils.IdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;

@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String address;

    @ManyToOne
    @JoinColumn(name = "city_id")
    @JsonSerialize(using = IdSerializer.class)
    private City city;
}
