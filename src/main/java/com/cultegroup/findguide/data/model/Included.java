package com.cultegroup.findguide.data.model;

import com.cultegroup.findguide.data.utils.IdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;

@Entity
public class Included {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "food_description")
    private String foodDescription;
    @Column(name = "drinks_description")
    private String drinkDescription;
    @Column(name = "tickets_description")
    private String ticketDescription;
    @Column(name = "travel_description")
    private String travelDescription;
    @Column(name = "inventory_description")
    private String inventoryDescription;
    @Column(name = "else_description")
    private String elseDescription;

    @OneToOne
    @JoinColumn(name = "experience_id")
    @JsonSerialize(using = IdSerializer.class)
    private Experience experience;

    public Included() {
    }

    public Included(
            String foodDescription,
            String drinkDescription,
            String ticketDescription,
            String travelDescription,
            String inventoryDescription,
            String elseDescription,
            Experience experience
    ) {
        this.foodDescription = foodDescription;
        this.drinkDescription = drinkDescription;
        this.ticketDescription = ticketDescription;
        this.travelDescription = travelDescription;
        this.inventoryDescription = inventoryDescription;
        this.elseDescription = elseDescription;
        this.experience = experience;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getDrinkDescription() {
        return drinkDescription;
    }

    public void setDrinkDescription(String drinkDescription) {
        this.drinkDescription = drinkDescription;
    }

    public String getTicketDescription() {
        return ticketDescription;
    }

    public void setTicketDescription(String ticketDescription) {
        this.ticketDescription = ticketDescription;
    }

    public String getTravelDescription() {
        return travelDescription;
    }

    public void setTravelDescription(String travelDescription) {
        this.travelDescription = travelDescription;
    }

    public String getInventoryDescription() {
        return inventoryDescription;
    }

    public void setInventoryDescription(String inventoryDescription) {
        this.inventoryDescription = inventoryDescription;
    }

    public String getElseDescription() {
        return elseDescription;
    }

    public void setElseDescription(String elseDescription) {
        this.elseDescription = elseDescription;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }
}
