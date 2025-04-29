package com.example.MovieBookingApplication.Service;

import com.example.MovieBookingApplication.DTO.BookingDTO;
import com.example.MovieBookingApplication.Entity.Booking;
import com.example.MovieBookingApplication.Entity.BookingStatus;
import com.example.MovieBookingApplication.Entity.Show;
import com.example.MovieBookingApplication.Entity.User;
import com.example.MovieBookingApplication.Repository.BookingRepository;
import com.example.MovieBookingApplication.Repository.ShowRepository;
import com.example.MovieBookingApplication.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingService {

      @Autowired
      private BookingRepository bookingRepository;

      @Autowired
      private ShowRepository showRepository;

      @Autowired
      private UserRepository userRepository;


    public Booking createBooking(BookingDTO bookingDTO) {

        Show show = showRepository.findById(bookingDTO.getShowId())
                .orElseThrow(()->new RuntimeException("show not found"));

        if(!isSeatAvailable(show.getId(), bookingDTO.getNumberofSeats())){
             throw new RuntimeException("No enough Seat Available ");

        }

        if(bookingDTO.getSeatNumbers().size() != bookingDTO.getNumberofSeats()){
             throw new RuntimeException("seat Number and Number of seat must be equal");
        }

        validateDuplicateSeats(show.getId(),bookingDTO.getSeatNumbers());

        User user = userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(()-> new RuntimeException("User not found"));

        Booking booking = new Booking();

        booking.setUser(user);
        booking.setShow(show);
        booking.setSeatNumbers(bookingDTO.getSeatNumbers());
        booking.setBookingStatus(BookingStatus.PANDING);
        booking.setNumberofSeats(bookingDTO.getNumberofSeats());
        booking.setPrice(calculateTotalAmount(show.getPrice(),bookingDTO.getNumberofSeats()));
        booking.setBookingTime(LocalDateTime.now());


        return bookingRepository.save(booking);

    }

    private Double calculateTotalAmount(Double price, Integer numberofSeats) {

          return price*numberofSeats;
    }

    private void validateDuplicateSeats(Long showId, List<String> seatNumbers) {
        Show show = showRepository.findById(showId)
                .orElseThrow(()->new RuntimeException("show not found"));

        Set<String> occupiedSeats = show.getBookings().stream()
                         .filter(b->b.getBookingStatus() != BookingStatus.CANCELLED)
                         .flatMap(b->b.getSeatNumber().stream())
                .collect(Collectors.toSet());

        List<String>duplicateSeats = seatNumbers.stream()
                .filter(occupiedSeats::contains)
                .collect(Collectors.toList());


        if(!duplicateSeats.isEmpty()){
            throw new RuntimeException("seats are already booked for someone else");
        }


    }

    private boolean isSeatAvailable(Long showId, Integer numberOfSeats) {

        Show show = showRepository.findById(showId)
                .orElseThrow(()->new RuntimeException("show not found"));

        int bookedSeats = show.getBookings().stream()
                .filter(booking -> booking.getBookingStatus() != BookingStatus.CANCELLED)
                .mapToInt(Booking::getNumberofSeats)
                .sum();


        return (show.getTheater().getTheaterCapacity() - bookedSeats) >= numberOfSeats;

    }

    public List<Booking> getUserBookings(Long userId) {

        return bookingRepository.findByUserId(userId);

    }

    public List<Booking> getShowBookings(Long showId) {

         return bookingRepository.findByShowId(showId);
    }

    public Booking confirmBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->new RuntimeException("Booking not found"));

        if(booking.getBookingStatus() != BookingStatus.PANDING){
             throw new RuntimeException("booking Status is not in pending Status");
        }

        // Payment APi process;

        booking.setBookingStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);

    }

    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->new RuntimeException("Booking not found"));

        validateCancellation(booking);

        booking.setBookingStatus(BookingStatus.CANCELLED);

        return bookingRepository.save(booking);
    }

    private void validateCancellation(Booking booking) {

        LocalDateTime showTime = booking.getShow().getShowTime();
        LocalDateTime deadLine = showTime.minusHours(2);

        if(LocalDateTime.now().isAfter(deadLine)){
             throw new RuntimeException("can't be cancled ");
        }

        if(booking.getBookingStatus() == BookingStatus.CANCELLED){
             throw new RuntimeException("Already canceled");
        }


    }

    public List<Booking> getBookingByStatus(BookingStatus bookingStatus) {

          return bookingRepository.findByBookingStatus(bookingStatus);
    }
}
