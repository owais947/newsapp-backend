package com.navi.assignment2.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "users")
public class User {
    private String email;
    private String selectedCategory;
    private String selectedCountry;
    private String preferredSources;
    private boolean subscribed = true;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user")
    private List<Endpoint> endpoints;


    public User(String email, String selectedCategory, String selectedCountry, String preferredSources) {
        this.email = email;
        this.selectedCategory = selectedCategory;
        this.selectedCountry = selectedCountry;
        this.preferredSources = preferredSources;
    }
}
