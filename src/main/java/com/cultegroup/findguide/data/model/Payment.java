package com.cultegroup.findguide.data.model;

import com.cultegroup.findguide.data.utils.IdSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "payments")
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class Payment {

    private static final int GROUP_SIZE = 0;
    private static final int PRICE_PER_ONE = 1;

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

    // Данные должны быть в формате [groupSize]:[PricePerOne]
    @JsonIgnore
    @Type(type = "list-array")
    @Column(
            name = "discounts",
            columnDefinition = "text[]"
    )
    private List<String> discounts;

    @OneToOne
    @JoinColumn(name = "experience_id")
    @JsonSerialize(using = IdSerializer.class)
    private Experience experience;

    public Payment() {
    }

    public Payment(
            int priceSolo,
            int priceGroup,
            int minGroupSize,
            int maxGroupSize,
            Experience experience,
            List<String> discounts
    ) {
        this.priceSolo = priceSolo;
        this.priceGroup = priceGroup;
        this.minGroupSize = minGroupSize;
        this.maxGroupSize = maxGroupSize;
        this.experience = experience;
        this.discounts = discounts;
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

    public List<String> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<String> discounts) {
        this.discounts = discounts;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }

    public Map<Integer, Integer> getPrices() {
        Map<Integer, Integer> result = new HashMap<>();
        for (String discount : discounts) {
            String[] parsed = discount.split(":");
            result.put(Integer.valueOf(parsed[GROUP_SIZE]), Integer.valueOf(parsed[PRICE_PER_ONE]));
        }
        return result;
    }
}
