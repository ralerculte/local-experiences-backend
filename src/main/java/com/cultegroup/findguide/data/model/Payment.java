package com.cultegroup.findguide.data.model;

import com.cultegroup.findguide.data.utils.IdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "price_solo")
    private int priceSolo;
    @Column(name = "price_group")
    private int priceGroup;
    @Column(name = "min_group_size")
    private int minGroupSize;
    @Column(name = "max_group_size")
    private int maxGroupSize;

    @OneToOne
    @JoinColumn(name = "experience_id")
    @JsonSerialize(using = IdSerializer.class)
    private Experience experience;

    public Payment() {
    }

    public Payment(int priceSolo, int priceGroup, int minGroupSize, int maxGroupSize, Experience experience) {
        this.priceSolo = priceSolo;
        this.priceGroup = priceGroup;
        this.minGroupSize = minGroupSize;
        this.maxGroupSize = maxGroupSize;
        this.experience = experience;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPriceSolo() {
        return priceSolo;
    }

    public void setPriceSolo(int priceSolo) {
        this.priceSolo = priceSolo;
    }

    public int getPriceGroup() {
        return priceGroup;
    }

    public void setPriceGroup(int priceGroup) {
        this.priceGroup = priceGroup;
    }

    public int getMinGroupSize() {
        return minGroupSize;
    }

    public void setMinGroupSize(int minGroupSize) {
        this.minGroupSize = minGroupSize;
    }

    public int getMaxGroupSize() {
        return maxGroupSize;
    }

    public void setMaxGroupSize(int maxGroupSize) {
        this.maxGroupSize = maxGroupSize;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }
}
