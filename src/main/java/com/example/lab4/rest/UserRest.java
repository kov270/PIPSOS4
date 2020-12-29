package com.example.lab4.rest;

import com.example.lab4.db.UserRepositoryJPA;
import com.example.lab4.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

@CrossOrigin(origins = "http://localhost:9824")
@RestController
@RequestMapping("/user")
public class UserRest {

    UserRepositoryJPA dbUsers;
    
    @Autowired
    Session session;

    @Autowired
    public UserRest(UserRepositoryJPA dbUsers) {
        this.dbUsers = dbUsers;
    }

    @PostMapping("/login")
    public ResponseEntity<MyResponse> login(@RequestBody MyRequest request) {
        MyResponse resp = new MyResponse();
        resp.status = MyResponse.statusOk;
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(request.getPassword().getBytes(StandardCharsets.UTF_8));
            String encodedPass = Base64.getEncoder().encodeToString(hash);

            List<User> users = this.dbUsers.findAll();

            for (User user : users) {
                if (request.getUsername().equals(user.getUsername()) &&
                        encodedPass.equals(user.getPasswordHash())) {
                    resp.key = session.getNewKey();
                    return ResponseEntity.ok(resp);
                }
            }
            throw new Exception("Invalid username or pass");

        } catch (Exception e) {
            resp.status = MyResponse.statusFail;
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register")
    public ResponseEntity<MyResponse> register(@RequestBody MyRequest request) {
        MyResponse resp = new MyResponse();
        resp.status = MyResponse.statusOk;
        try {

            if (!this.alreadyRegistered(request.getUsername())) {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(request.getPassword().getBytes(StandardCharsets.UTF_8));
                String encodedPass = Base64.getEncoder().encodeToString(hash);
                User user = new User();
                user.setUsername(request.getUsername());
                user.setPasswordHash(encodedPass);

                this.dbUsers.save(user);
                resp.key = session.getNewKey();
            } else
                throw new Exception("Already registered");

        } catch (Exception e) {
            resp.status = MyResponse.statusFail;
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<MyResponse> logout(@RequestBody MyRequest request) {
        MyResponse resp = new MyResponse();
        resp.status = MyResponse.statusOk;
        session.deleteKey(request.getKey());
        return ResponseEntity.ok(resp);
    }

    private Boolean alreadyRegistered(String username) {
        List<User> users = this.dbUsers.findAll();

        for (User user : users) {
            if (username.equals(user.getUsername()))
                return true;
        }
        return false;
    }

}
