package com.quinbay.authServer.controller;


import com.quinbay.authServer.dataModels.requests.CustomUser;
import com.quinbay.authServer.dataModels.response.StandardApiResponse;
import com.quinbay.authServer.dataModels.requests.RegisterRequest;
import com.quinbay.authServer.service.MyUserDetailsService;
import com.quinbay.authServer.service.UserRolesService;
import com.quinbay.authServer.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthenticationController {


    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;


    @Autowired
    private UserRolesService userRolesService;

    @Autowired
    private JwtUtil jwtTokenUtil;



    // test method
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity<?> test() {

        Map<String, Object> data = new HashMap<>();
        data.put( "message", "working fine" );

        return ResponseEntity.ok( new StandardApiResponse( true, "none", data ) );
    }


    /*
    *   /authenticate would take email and password provided
    *   validates the email and password against the entries in the database
    *   Returns a standard response template with jwt in the data key
    */

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody CustomUser customUser) {


        try {
            authenticationManager.authenticate( new UsernamePasswordAuthenticationToken( customUser.getUsername(), customUser.getPassword() ) );
        } catch (BadCredentialsException e) {

            Map<String, Object> data = new HashMap<>();

            return ResponseEntity.ok( new StandardApiResponse( false, "Incorrect Username and Password", data ) );
        }


        final UserDetails userDetails = userDetailsService.loadUserByUsername( customUser.getUsername() );

        final String jwt = jwtTokenUtil.generateToken( userDetails );

        Map<String, Object> data = new HashMap<>();

        data.put( "jwt", jwt );

        return ResponseEntity.ok( new StandardApiResponse( true, "", data ) );

    }

    /*
    *   /register would register a user and make an entry in the database
    *   save the roles that user is identified as on various platforms
    *   Forward the interests provided in the body to Data Analytics team through their api
     */

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest registerRequest){

        Map<String, Object> data = new HashMap<>();


        try{

            // make a call to store username and password in postgres database
            myUserDetailsService.save(registerRequest.toAuthenticationRequest());


            final UserDetails userDetails = userDetailsService.loadUserByUsername( registerRequest.toAuthenticationRequest().getUsername() );

            final String jwt = jwtTokenUtil.generateToken( userDetails );
            data.put( "jwt", jwt);


            // make a call to store the roles in roles database
            try{
                userRolesService.addRoles( registerRequest.roles, registerRequest.getUsername());
                System.out.println(registerRequest.roles);
            }
            catch(Exception e){
                data.put( "message", "Failed to add user roles" );
            }

            // make a call to store the interests [API provided by Data Team]


        }catch(Exception e){

            Map<String, Object> errorData = new HashMap<>();
            return ResponseEntity.ok( new StandardApiResponse( false, "User Registration Failed", errorData ) );
        }


        return ResponseEntity.ok( new StandardApiResponse( true, "", data ) );

    }

    // Delete Account API Pending


    // Get Username
    @RequestMapping(value = "/getUsername", method = RequestMethod.GET)
    public ResponseEntity<?> getUserName(@AuthenticationPrincipal CustomUser user){

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").split(" ")[1];

        Map<String, Object> data = new HashMap<>();

        try{
                data.put( "username", jwtTokenUtil.extractUsername(token));
        }
        catch(Exception e){
            Map<String, Object> errorData = new HashMap<>();
            return ResponseEntity.ok( new StandardApiResponse( false, "Can't Fetch User Details with the Provided Token", errorData ) );
        }

        return ResponseEntity.ok( new StandardApiResponse( true, "", data ) );
    }


    // Check If Token is valid
    @RequestMapping(value = "/validateToken", method = RequestMethod.GET)
    public ResponseEntity<?> validateToken(@AuthenticationPrincipal CustomUser user){

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").split(" ")[1];

        Map<String, Object> data = new HashMap<>();

        try{
            data.put( "valid", jwtTokenUtil.validateToken(token, userDetailsService.loadUserByUsername( jwtTokenUtil.extractUsername(token) )));
        }
        catch(Exception e){
            Map<String, Object> errorData = new HashMap<>();
            return ResponseEntity.ok( new StandardApiResponse( false, "Token Invalid", errorData ) );
        }

        return ResponseEntity.ok( new StandardApiResponse( true, "", data ) );

    }

}
