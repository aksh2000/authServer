package com.quinbay.authServer.service;

import com.quinbay.authServer.dataModels.requests.UserRoles;
import com.quinbay.authServer.repository.UserRolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRolesService {
    @Autowired
    UserRolesRepository userRolesRepository;




    public boolean saveUserRoles(UserRoles userRoles){

        /*
         * TO SAVE THE USER ROLES IN MONGO DB COLLECTION AGAINST THE USERNAME PROVIDED
         */

        try{
            userRolesRepository.save( userRoles );
        }
        catch(Exception e){
            return false;
        }

        return true;
    }





    public boolean deleteRole(String role, String username){

        /*
         * TO REMOVE A PARTICULAR USER ROLE BASED ON USERNAME
         */

        UserRoles userRoles =  userRolesRepository.findByUsername( username );
        List<String> roles = userRoles.getRoles();
        roles.remove(role);
        userRoles.setRoles(roles);


        try{
            userRolesRepository.save( userRoles );
        }
        catch(Exception e){
            return false;
        }

        return true;

    }



    public boolean addRoles(List<String> roles, String username){

        /*
         * TO ADD A USER ROLEs BASED ON USERNAME
         * This method would be available to other services to add new roles
         */

        UserRoles userRoles = userRolesRepository.findByUsername( username );

        try {

            if (userRoles != null) {

                for (String role : roles) {
                    userRoles.getRoles().add( role );
                }

                System.out.println("userRoles Before Adding to the database");
                System.out.println(userRoles.getRoles());
                userRolesRepository.save( userRoles );

                return true;
            } else {
                userRoles = new UserRoles();

                userRoles.setUsername( username );
                userRoles.setRoles( roles );

                System.out.println("userRoles Before Adding to the database");
                System.out.println(userRoles.getRoles());
                userRolesRepository.save( userRoles );

                UserRoles userRoles1 = userRolesRepository.findByUsername( username );

                System.out.println("After adding to the database");
                System.out.println(userRoles1.getRoles());


                return true;
            }
        } catch(Exception e){


            System.out.println("Error was thrown" + e.toString());
            return false;
        }
    }



    public List<String> getUserRoles(String username){
        /*
         * TO GET USER ROLEs BASED ON USERNAME
         */

        UserRoles userRoles = userRolesRepository.findByUsername( username );


        if(userRoles != null){
            return userRoles.getRoles();
        }
        else{
            return new ArrayList<String>();
        }

    }


    public String getUserRolesAsJson(String username){

        String jsonString;
        List<String> userRoles = getUserRoles( username );

        jsonString = "{ 'userRoles' : ['";

        jsonString += String.join( "','", userRoles );

        jsonString += "']}";

        return jsonString;

    }




}
