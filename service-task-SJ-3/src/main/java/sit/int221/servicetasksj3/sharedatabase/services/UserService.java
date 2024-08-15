package sit.int221.servicetasksj3.sharedatabase.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.servicetasksj3.sharedatabase.dtos.UserDTO;
import sit.int221.servicetasksj3.sharedatabase.entities.UsersEntity;
import sit.int221.servicetasksj3.sharedatabase.repositories.UserRepository;
import java.util.List;

@Service
public class UserService {

@Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    public List<UsersEntity> getAllUsers() {
       return userRepository.findAll();
    }

    @Transactional
    public UsersEntity createNewUsers(UserDTO userDTO){
        UsersEntity users = modelMapper.map(userDTO, UsersEntity.class);
        return userRepository.save(users);
    }
}
