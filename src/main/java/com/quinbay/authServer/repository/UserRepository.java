package com.quinbay.authServer.repository;

import com.quinbay.authServer.dataModels.requests.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<CustomUser, Long> {

    // method to find the user based on username

    CustomUser findByUsername(String username);

}
