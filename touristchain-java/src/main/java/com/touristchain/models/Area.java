package com.touristchain.models;

import jakarta.persistence.*;

@Entity
@Table(name = "area")
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Area_id")
    private Long id;

    @Column(name = "Country_id")
    private Long countryId;

    @Column(name = "AreaName")
    private String areaName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCountryId() { return countryId; }
    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
}
