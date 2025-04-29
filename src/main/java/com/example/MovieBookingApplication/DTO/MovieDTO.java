package com.example.MovieBookingApplication.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieDTO {

    private String name;
    private String genre;
    private LocalDate releaseDate;
    private String description;
    private Integer duration;
    private String Language;

}
