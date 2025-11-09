package com.touristchain.models;

import jakarta.persistence.*;

@Entity
@Table(name = "country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Country_id")
    private Long id;

    @Column(name = "Trip_id")
    private Long tripId;

    @Column(name = "CountryName")
    private String countryName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
}
