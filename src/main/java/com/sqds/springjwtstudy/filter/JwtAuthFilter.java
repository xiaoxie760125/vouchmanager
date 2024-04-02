package com.sqds.springjwtstudy.filter;

import com.sqds.springjwtstudy.jwtutil.modal.JwtService;
import com.sqds.springjwtstudy.service.dataservice;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;
    @Autowired
    private dataservice userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //get the token from the header
        var headers=request.getHeaderNames();

        String authHeader = request.getHeader("Authorization");
        String token = null;

      if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
           try {
                //get the user from the token
                var username = jwtService.exractUsername(token);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (userDetails != null) {

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    chain.doFilter(request, response);
                    return;

                }
           } catch (Exception e) {

            chain.doFilter(request,response);
              // throw new RuntimeException(e.getMessage());

            }

        }
        chain.doFilter(request,response);
    }




}
