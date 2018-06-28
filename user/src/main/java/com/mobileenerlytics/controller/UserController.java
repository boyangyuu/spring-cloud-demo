package com.mobileenerlytics.controller;

import com.mobileenerlytics.config.Constants;
import com.mobileenerlytics.entity.User;
import com.mobileenerlytics.repository.UserRepository;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("auth") // todo change the signin to auth in frontend
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoDatabase mongoDatabase; // ?

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private RestTemplate restTemplate; // consume other api.

    @GetMapping
    public Response signin(@RequestHeader(HttpHeaders.AUTHORIZATION) String credentials) {
        // check
        String username, password, email;
        if(credentials == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        String[] userPass = credentials.split(" ");
        username = userPass[0];
        password = userPass[1];
        email = userPass[2];
        User user = userRepository.findUserByUsername(username);
        if (user == null) return Response.status(Response.Status.UNAUTHORIZED).entity("username not exist").build();
        if (user.isPassword(password)) return Response.status(Response.Status.UNAUTHORIZED).entity("password not correct").build();

        // token
        Date exp = new Date(System.currentTimeMillis() + (1000 * 30)); //  todo 30 seconds
        long expiredDate = exp.getTime();
        String strCombined = "username:" + expiredDate + ":mobileenergy"; // todo need sign
        byte[] message = strCombined.getBytes(StandardCharsets.UTF_8);
        String token = Base64.getEncoder().encodeToString(message);
        LOGGER.info("token is build: ", token);
        return Response.ok().entity(token).build();
    }

    @PostMapping
    public Response signup(
                         @RequestHeader(HttpHeaders.AUTHORIZATION) String credentials,
                         @RequestParam("org") String org,
                         @RequestParam("firstname") String firstname,
                         @RequestParam("lastname") String lastname,
                         @RequestParam("role") String role
    ) {
        Set<String> roles = new HashSet<>(Arrays.asList(Constants.USER_ROLE_DEFAULT, Constants.USER_ROLE_DEMO, Constants.USER_ROLE_MANUAL));
        if (!roles.contains(role)) role = Constants.USER_ROLE_DEFAULT;

        // username:password:email format accepted
        final String[] credentialsSplit = credentials.split(":");
        if (credentialsSplit.length != 3) {
            LOGGER.error("SignupService attempted with incomplete data in auth header.");
            return Response.noContent().build();
        }

        String username = credentialsSplit[0];
        String password = credentialsSplit[1];
        String email = credentialsSplit[2];

        if (userRepository.findUserByUsername(username) != null
                || userRepository.findUserByEmail(email) != null) {
            String msg = "SignupService attempted with existing username.";
            LOGGER.error(msg);
            return Response.status(Response.Status.CONFLICT).entity(msg).build();
        }

        if (username.length() < 4) {
            String msg = "SignupService attempted with username smaller than 4 characters.";
            LOGGER.error(msg);
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).build();
        }

        if (password.length() < 8) {
            String msg = "SignupService attempted with password smaller than 8 characters.";
            LOGGER.error(msg);
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).build();
        }
        boolean success = true;
        try {
            User user = new User(email, username, firstname, lastname, password, org, new Date());
            user.setRole(role);
            user = userRepository.save(user);
            success = user != null;
        } catch (Exception e){
            LOGGER.error("Encountered Error:" + e);
            success = false;
        }

        if(!success)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        // todo crm
//        Map<String, String> properties = new HashMap<>();
//        properties.put(CRMLogger.PROP_EMAIL, email);
//        properties.put(CRMLogger.PROP_FIRSTNAME, firstname);
//        properties.put(CRMLogger.PROP_LASTNAME, lastname);
//        properties.put(CRMLogger.PROP_COMPANY, org);
//        properties.put(CRMLogger.PROP_ROLE, role);
//
//        CRMLogger.logPropertyUpdate(email, properties);
//        CRMLogger.logLifecycleEvent(email, CRMLogger.CRMLifecyleEvent.SIGNUP);

        return Response.ok().build();
    }

}
