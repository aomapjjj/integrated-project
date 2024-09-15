package sit.int221.servicetasksj3.services;

import io.viascom.nanoid.NanoId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.entities.Board;
import sit.int221.servicetasksj3.entities.TaskLimit;
import sit.int221.servicetasksj3.entities.TaskStatus;
import sit.int221.servicetasksj3.exceptions.InternalServerErrorException;
import sit.int221.servicetasksj3.exceptions.UnauthorizedException;
import sit.int221.servicetasksj3.repositories.BoardRepository;
import sit.int221.servicetasksj3.repositories.LimitRepository;
import sit.int221.servicetasksj3.repositories.StatusRepository;
import sit.int221.servicetasksj3.sharedatabase.dtos.JwtRequestUser;
import sit.int221.servicetasksj3.sharedatabase.entities.AuthUser;
import sit.int221.servicetasksj3.sharedatabase.services.JwtTokenUtil;
import sit.int221.servicetasksj3.sharedatabase.services.JwtUserDetailsService;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationService {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private LimitRepository limitRepository;

    public String login(JwtRequestUser user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));

        if (!authentication.isAuthenticated()) {
            throw new UnauthorizedException("Username or Password is incorrect.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);
        String oid = ((AuthUser) userDetails).getOid();

//        try {
//            if (boardRepository.findByOwnerId(oid).isEmpty()) {
//                Board board = new Board();
//                board.setId(NanoId.generate(10));  // สร้าง id โดยใช้ NanoId
//                board.setOwnerId(oid);
//                board.setName("Default Board Name"); // ตั้งชื่อบอร์ดค่าเริ่มต้น (สามารถเปลี่ยนได้ในภายหลัง)
//
//                Board newBoard = boardRepository.save(board);
//                List<TaskStatus> statuses = new ArrayList<>();
//                TaskStatus status1 = new TaskStatus("No Status","A status has not been assigned", newBoard.getId());
//                TaskStatus status2 = new TaskStatus("To Do","The task is included in the project", newBoard.getId());
//                TaskStatus status3 = new TaskStatus("In Progress","The task is being worked on", newBoard.getId());
//                TaskStatus status4 = new TaskStatus("Done","The task has been completed", newBoard.getId());
//                statuses.add(status1);
//                statuses.add(status2);
//                statuses.add(status3);
//                statuses.add(status4);
//                statusRepository.saveAll(statuses);
//
//                TaskLimit limit = new TaskLimit(10,false, newBoard.getId());
//                limitRepository.save(limit);
//            }
//        } catch (Exception e) {
//            throw new InternalServerErrorException("Cannot create user: " + e.getMessage());
//        }
        return token;
    }
}

