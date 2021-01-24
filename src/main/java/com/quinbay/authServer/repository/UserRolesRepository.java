package com.quinbay.authServer.repository;


import com.quinbay.authServer.dataModels.requests.UserRoles;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRolesRepository extends MongoRepository<UserRoles, Long> {


    UserRoles findByUsername(String username);

}
