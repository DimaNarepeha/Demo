package com.softserve.demo.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "service_provider")
public class Provider {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "description")
    private String description;

    @Column(name = "path_to_photo")
    private String image;

    @CreationTimestamp
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "last_update")
    private Date lastUpdate;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "provider",cascade = CascadeType.REMOVE)
    private List<Order> orders;

    @ManyToMany(cascade = CascadeType.REMOVE ,fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinTable(
            name = "provider_service",
            joinColumns = {@JoinColumn(name = "provider_id")},
            inverseJoinColumns = {@JoinColumn(name = "service_id")}
    )
    private List<Service> services = new ArrayList<>();

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

}
