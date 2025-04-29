package com.example.MovieBookingApplication.Controller;


import com.example.MovieBookingApplication.DTO.ShowDTO;
import com.example.MovieBookingApplication.Entity.Show;
import com.example.MovieBookingApplication.Service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/show")
public class ShowController {

    @Autowired
    private ShowService showService;

    @PostMapping("/createshow")
    public ResponseEntity<Show> createShow(@RequestBody ShowDTO showDTO){

        return  ResponseEntity.ok(showService.createShow(showDTO));
    }

    @GetMapping("/getallshows")
    public ResponseEntity<List<Show>> getAllShows(){
         return ResponseEntity.ok(showService.getAllShows());
    }

    @GetMapping("/getshowsbymovie/{id}")
    public ResponseEntity<List<Show>> getShowsByMovie(@PathVariable Long id){

         return ResponseEntity.ok(showService.getShowsByMovie(id));
    }

    @GetMapping("/getshowsbytheater/{id}")
    public ResponseEntity<List<Show>> getShowsByTheater(@PathVariable Long id){

         return ResponseEntity.ok(showService.getShowsByTheater(id));
    }

    @PutMapping("/updateshow/{id}")
    @PreAuthorize("has_Role('ADMIN')")
    public ResponseEntity<Show> updateShow(@PathVariable Long id, @RequestBody ShowDTO showDTO){

        return ResponseEntity.ok(showService.updateShow(id,showDTO));
    }

    @DeleteMapping("/deleteshow/{id}")
    @PreAuthorize("has_Role('ADMIN')")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id){

        showService.deleteShow(id);
         return ResponseEntity.ok().build();
    }

}
