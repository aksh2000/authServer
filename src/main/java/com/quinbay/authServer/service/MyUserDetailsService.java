package com.quinbay.authServer.service;

import com.quinbay.authServer.dataModels.requests.CustomUser;
import com.quinbay.authServer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {


    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        CustomUser user = userRepository.findByUsername(username);

        return new User(user.getUsername(),user.getPassword(),new ArrayList<>() );
    }

    public CustomUser save(CustomUser customUser){

        return userRepository.save( customUser );
    }
}
