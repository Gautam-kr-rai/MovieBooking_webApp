package com.example.MovieBookingApplication.JWT;

import com.example.MovieBookingApplication.Repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTAuthenticationFilter  extends OncePerRequestFilter {

    @Autowired
     private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwtToken ;
        final String userName;


        if(authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }

        // extract the jwt token from the header
        jwtToken = authHeader.substring(7);
        userName = jwtService.extractUserName(jwtToken);

        // check
        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
            var userDetails = userRepository.findByUsername(userName)
                    .orElseThrow(()->new RuntimeException("user Not found"));


            if(jwtService.isTokenValid(jwtToken,userDetails)){

                List<SimpleGrantedAuthority> authorities = userDetails.getRole()
                        .stream().
                        map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken = new
                        UsernamePasswordAuthenticationToken(userDetails,null,authorities);
// set authentication details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // update security context with authentication

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }




        }

         filterChain.doFilter(request,response);
    }
}
