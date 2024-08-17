package sit.int221.servicetasksj3.sharedatabase.services;


import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.servicetasksj3.sharedatabase.dtos.UserDTO;
import sit.int221.servicetasksj3.sharedatabase.entities.Users;
import sit.int221.servicetasksj3.sharedatabase.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    //Get all users
    @Transactional
    public List<Users> getAllUser(){
        return userRepository.findAll();
    }

    @Transactional
    public boolean Authentication(String username, String password) {
        Users users = userRepository.findByUsername(username);
        if (users == null){
            return false;
        } else {
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 32);
            char[] passwordArray = password.toCharArray();
            return argon2.verify(users.getPassword(), passwordArray);
        }
    }
}
