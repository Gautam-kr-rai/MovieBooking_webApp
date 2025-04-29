package com.example.MovieBookingApplication.Service;

import com.example.MovieBookingApplication.DTO.ShowDTO;
import com.example.MovieBookingApplication.Entity.Booking;
import com.example.MovieBookingApplication.Entity.Movie;
import com.example.MovieBookingApplication.Entity.Show;
import com.example.MovieBookingApplication.Entity.Theater;
import com.example.MovieBookingApplication.Repository.MovieRepository;
import com.example.MovieBookingApplication.Repository.ShowRepository;
import com.example.MovieBookingApplication.Repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowService {
    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    public Show createShow(ShowDTO showDTO) {

        Movie movie = movieRepository.findById(showDTO.getMovieId())
                .orElseThrow(()->new RuntimeException("No Movie Found for id" +showDTO.getMovieId() ));

        Theater theater = theaterRepository.findById(showDTO.getTheaterId())
                .orElseThrow(()->new RuntimeException("No Theater found for id " + showDTO.getTheaterId()));


        Show show = new Show();
        show.setShowTime(showDTO.getShowTime());
        show.setMovie(movie);
        show.setPrice(showDTO.getPrice());
        show.setTheater(theater);

       return showRepository.save(show);

    }

    public List<Show> getAllShows() {
       return showRepository.findAll();
    }

    public List<Show> getShowsByMovie(Long movie_id) {
        Optional<List<Show>> showListBox =  showRepository.findByMovieId(movie_id);

        if(showListBox.isPresent()){
             return showListBox.get();
        }
        else throw new RuntimeException("No show found for this movie_id"+movie_id);
    }

    public List<Show> getShowsByTheater(Long theater_id) {
        Optional<List<Show>> showListBox =  showRepository.findByTheaterId(theater_id);
        if(showListBox.isPresent()){
            return showListBox.get();
        }
        else throw new RuntimeException("No show found for this movie_id"+theater_id);
    }

    public Show updateShow(Long id, ShowDTO showDTO) {

        Show show = showRepository.findById(id)
                .orElseThrow(()->new RuntimeException("No show found for this id hence it can't be update"));

        Movie movie = movieRepository.findById(showDTO.getMovieId())
                .orElseThrow(()->new RuntimeException("No Movie Found for id" +showDTO.getMovieId() ));

        Theater theater = theaterRepository.findById(showDTO.getTheaterId())
                .orElseThrow(()->new RuntimeException("No Theater found for id " + showDTO.getTheaterId()));

        show.setShowTime(showDTO.getShowTime());
        show.setMovie(movie);
        show.setPrice(showDTO.getPrice());
        show.setTheater(theater);

        return showRepository.save(show);
    }

    public void deleteShow(Long id) {
           if(!showRepository.existsById(id)){
                throw new RuntimeException("No show Available for this id"+id);
           }

           List<Booking> bookings = showRepository.findById(id).get().getBookings();

           if(!bookings.isEmpty()){
               throw new RuntimeException("can't be deleted their is bookings");
           }

           showRepository.deleteById(id);
    }
}
