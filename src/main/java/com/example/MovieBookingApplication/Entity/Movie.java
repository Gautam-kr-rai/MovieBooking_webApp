package com.example.MovieBookingApplication.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
//@Table(name="movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      private String name;
      private String genre;
      private LocalDate releaseDate;
      private String description;
      private Integer duration;
      private String language;

      @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
      // Lazy means we don't want to load the show when movie get loaded
      private List<Show> show;

}
