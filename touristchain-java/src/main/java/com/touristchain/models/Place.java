package com.touristchain.models;

import jakarta.persistence.*;

@Entity
@Table(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Place_id")
    private Long id;

    @Column(name = "Country_id")
    private Long countryId;

    @Column(name = "PlaceName")
    private String placeName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCountryId() { return countryId; }
    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }
}
