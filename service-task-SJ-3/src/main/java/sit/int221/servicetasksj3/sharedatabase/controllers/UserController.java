package sit.int221.servicetasksj3.sharedatabase.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sit.int221.servicetasksj3.sharedatabase.dtos.UserDTO;
import sit.int221.servicetasksj3.sharedatabase.entities.Users;
import sit.int221.servicetasksj3.sharedatabase.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = { "http://localhost:5173/", "http://ip23sj3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th" } )

public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public List<Users> getAllUsers(){
        return userService.getAllUser();
    }

    @PostMapping("")
    public boolean Authentication(@Valid  @RequestBody UserDTO user) {
        return userService.Authentication(user.getUserName(), user.getPassword());

//        if (authenticatedUser != null) {
//            return ResponseEntity.ok(authenticatedUser);
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//        }
//    }

    }
}
