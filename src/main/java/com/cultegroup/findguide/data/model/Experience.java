package com.cultegroup.findguide.data.model;

import com.cultegroup.findguide.data.model.enums.Duration;
import com.cultegroup.findguide.data.model.enums.ExperienceType;
import com.cultegroup.findguide.data.model.enums.Language;
import com.cultegroup.findguide.data.model.location.Location;
import com.cultegroup.findguide.data.utils.EntityId;
import com.cultegroup.findguide.data.utils.IdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "experiencesV3")
public class Experience implements EntityId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Column(name = "is_approved")
    private Boolean isApproved;
    @Enumerated
    @Column(name = "experience_type")
    private ExperienceType experienceType;
    @Enumerated
    private Duration duration;
    @Type(ListArrayType.class)
    @Column(
            name = "media_links",
            columnDefinition = "text[]"
    )
    private List<String> mediaLinks;
    @Type(ListArrayType.class)
    @Column(
            name = "client_inventory",
            columnDefinition = "text[]"
    )
    private List<String> clientInventory;
    @Type(
            value = ListArrayType.class,
            parameters = {
                    @org.hibernate.annotations.Parameter(
                            name = ListArrayType.SQL_ARRAY_TYPE,
                            value = "sensor_state"
                    )
            }
    )
    @Column(
            name = "languages",
            columnDefinition = "language[]"
    )
    private List<Language> languages;


    @OneToOne(mappedBy = "experience", cascade = CascadeType.ALL)
    private GuideInfo guideInfo;
    @OneToOne(mappedBy = "experience", cascade = CascadeType.ALL)
    private Included included;
    @OneToOne(mappedBy = "experience", cascade = CascadeType.ALL)
    private Restriction restriction;
    @OneToOne(mappedBy = "experience", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Event> events = new HashSet<>();
    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ScheduledEvent> scheduledEvent = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonSerialize(using = IdSerializer.class)
    private User user;
    @ManyToOne
    @JoinColumn(name = "location_id")
    @JsonSerialize(using = IdSerializer.class)
    private Location location;

    public Experience() {
    }

    public Experience(
            String title,
            String description,
            Boolean isApproved,
            ExperienceType experienceType,
            Duration duration,
            List<String> mediaLinks,
            List<String> clientInventory,
            List<Language> languages,
            GuideInfo guideInfo,
            Included included,
            Restriction restriction,
            Payment payment,
            Set<Event> events,
            Set<ScheduledEvent> scheduledEvent,
            User user,
            Location location
    ) {
        this.title = title;
        this.description = description;
        this.isApproved = isApproved;
        this.experienceType = experienceType;
        this.duration = duration;
        this.mediaLinks = mediaLinks;
        this.clientInventory = clientInventory;
        this.languages = languages;
        this.guideInfo = guideInfo;
        this.included = included;
        this.restriction = restriction;
        this.payment = payment;
        this.events = events;
        this.scheduledEvent = scheduledEvent;
        this.user = user;
        this.location = location;
    }

    @Override
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

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public ExperienceType getExperienceType() {
        return experienceType;
    }

    public void setExperienceType(ExperienceType experienceType) {
        this.experienceType = experienceType;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public List<String> getMediaLinks() {
        return mediaLinks;
    }

    public void setMediaLinks(List<String> mediaLinks) {
        this.mediaLinks = mediaLinks;
    }

    public List<String> getClientInventory() {
        return clientInventory;
    }

    public void setClientInventory(List<String> clientInventory) {
        this.clientInventory = clientInventory;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public GuideInfo getGuideInfo() {
        return guideInfo;
    }

    public void setGuideInfo(GuideInfo guideInfo) {
        this.guideInfo = guideInfo;
    }

    public Included getIncluded() {
        return included;
    }

    public void setIncluded(Included included) {
        this.included = included;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Set<ScheduledEvent> getScheduledEvent() {
        return scheduledEvent;
    }

    public void setScheduledEvent(Set<ScheduledEvent> scheduledEvent) {
        this.scheduledEvent = scheduledEvent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
