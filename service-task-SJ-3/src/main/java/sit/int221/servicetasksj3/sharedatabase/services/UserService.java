package sit.int221.servicetasksj3.sharedatabase.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.servicetasksj3.sharedatabase.entities.Users;
import sit.int221.servicetasksj3.sharedatabase.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<Users> getAllUser(){
        return  userRepository.findAll();
    }


}
