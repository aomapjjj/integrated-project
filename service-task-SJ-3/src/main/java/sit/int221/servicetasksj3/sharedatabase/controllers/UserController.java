package sit.int221.servicetasksj3.sharedatabase.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.servicetasksj3.sharedatabase.dtos.UserDTO;
import sit.int221.servicetasksj3.sharedatabase.entities.UsersEntity;
import sit.int221.servicetasksj3.sharedatabase.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = { "http://localhost:5173/", "http://ip23sj3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th" } )
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    public List<UsersEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("")
    public ResponseEntity<UserDTO> createNewUsers(@RequestBody UserDTO users) {
        UsersEntity createUser = userService.createNewUsers(users);
        UserDTO createUserDTO = modelMapper.map(createUser, UserDTO.class);
        return  ResponseEntity.ok().body(createUserDTO);
    }
}
