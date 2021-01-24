package com.quinbay.authServer.filter;

import com.quinbay.authServer.service.MyUserDetailsService;
import com.quinbay.authServer.service.UserRolesService;
import com.quinbay.authServer.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsService myUserDetailsService;


    @Autowired
    private UserRolesService userRolesService;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = httpServletRequest.getHeader( "Authorization" );


        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith( "Bearer " )) {
            jwt = authorizationHeader.substring( 7 );
            username = jwtUtil.extractUsername( jwt );
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.myUserDetailsService.loadUserByUsername( username );


            if (jwtUtil.validateToken( jwt, userDetails )) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken( userDetails, null, userDetails.getAuthorities() );

                usernamePasswordAuthenticationToken.setDetails( new WebAuthenticationDetailsSource().buildDetails( httpServletRequest ) );

                SecurityContextHolder.getContext().setAuthentication( usernamePasswordAuthenticationToken );


                httpServletResponse.setHeader( "userName", jwtUtil.extractUsername( jwt ) );
                httpServletResponse.setHeader( "userRoles",  userRolesService.getUserRolesAsJson(jwtUtil.extractUsername( jwt )));

            }
        }





        filterChain.doFilter( httpServletRequest, httpServletResponse );
    }
}
