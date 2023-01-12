package com.cultegroup.localexperiences.data.model;

import com.cultegroup.localexperiences.data.utils.IdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "experiencesV2")
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String location;
    @Column(name = "min_age")
    private Integer minAge;
    @Column(name = "max_quantity")
    private Integer maxQuantity;
    @Enumerated
    @Column(name = "activity_level")
    private ActivityLevel activityLevel;
    @Enumerated
    private Category category;
    @Enumerated
    private Duration duration;

    @Type(type = "list-array")
    @Column(
            name = "media_links",
            columnDefinition = "text[]"
    )
    private List<String> mediaLinks;
    @Type(type = "list-array")
    @Column(
            name = "inventory",
            columnDefinition = "text[]"
    )
    private List<String> inventory;
    @Type(type = "list-array")
    @Column(
            name = "dates_start",
            columnDefinition = "timestamp[]"
    )
    private List<LocalDateTime> datesStart;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonSerialize(using = IdSerializer.class)
    private User user;
    @OneToMany(mappedBy = "experience")
    private Set<Event> events = new HashSet<>();

    public Experience() {
    }

    public Experience(
            String title,
            String description,
            String location,
            Integer minAge,
            Integer maxQuantity,
            ActivityLevel activityLevel,
            Category category,
            Duration duration,
            List<String> mediaLinks,
            List<String> inventory,
            List<LocalDateTime> datesStart,
            User user,
            Set<Event> events
    ) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.minAge = minAge;
        this.maxQuantity = maxQuantity;
        this.activityLevel = activityLevel;
        this.category = category;
        this.duration = duration;
        this.mediaLinks = mediaLinks;
        this.inventory = inventory;
        this.datesStart = datesStart;
        this.user = user;
        this.events = events;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public List<String> getMediaLinks() {
        return mediaLinks;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setMediaLinks(List<String> mediaLinks) {
        this.mediaLinks = mediaLinks;
    }

    public List<String> getInventory() {
        return inventory;
    }

    public void setInventory(List<String> inventory) {
        this.inventory = inventory;
    }

    public List<LocalDateTime> getDatesStart() {
        return datesStart;
    }

    public void setDatesStart(List<LocalDateTime> datesStart) {
        this.datesStart = datesStart;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        if (events == null) {
            events = new HashSet<>();
        }
        event.setExperience(this);
        events.add(event);
    }

    public List<String> getFeedback() {
        List<String> feedback = new ArrayList<>();
        for (Event event : events) {
            if (event.getFeedback() != null) {
                feedback.add(event.getFeedback());
            }
        }
        return feedback;
    }

    public double getRating() {
        int sum = 0;
        int count = 0;
        for (Event event : events) {
            if (event.getRating() != 0) {
                sum += event.getRating();
                count++;
            }
        }
        return count == 0 ? count : (double) sum / count;
    }
}
