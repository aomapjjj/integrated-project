package sit.int221.servicetasksj3.services;


import io.viascom.nanoid.NanoId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.servicetasksj3.entities.Board;
import sit.int221.servicetasksj3.repositories.BoardRepository;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    private String generateUniqueBoardId() {
        return NanoId.generate( 10);
    }




}


