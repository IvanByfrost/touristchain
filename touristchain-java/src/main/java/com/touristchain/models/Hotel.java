package com.touristchain.models;

import jakarta.persistence.*;

@Entity
@Table(name = "hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Hotel_id")
    private Long id;

    @Column(name = "HotelName")
    private String hotelName;

    @Column(name = "PlaceHotel")
    private String placeHotel;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public String getPlaceHotel() { return placeHotel; }
    public void setPlaceHotel(String placeHotel) { this.placeHotel = placeHotel; }
}
