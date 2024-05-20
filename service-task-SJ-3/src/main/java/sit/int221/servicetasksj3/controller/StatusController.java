package sit.int221.servicetasksj3.controller;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.servicetasksj3.dtos.StatusDTO;
import sit.int221.servicetasksj3.dtos.StatusDTOTwo;
import sit.int221.servicetasksj3.entities.TaskStatus;
import sit.int221.servicetasksj3.services.StatusService;

import java.util.List;

@RestController
@RequestMapping("/statuses")
@CrossOrigin(origins = { "http://localhost:5173/", "http://ip23sj3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th" } )

public class StatusController {
    @Autowired
    private StatusService service;
//    @Autowired
//    private ModelMapper modelMapper;

    @GetMapping("")
    public List<StatusDTOTwo> getAllStatuses() {
        return service.getAllStatuses();
    }

    @GetMapping("/{id}")
    public TaskStatus getStatusesById(@PathVariable Integer id){
        return service.getStatusesById(id);
    }

    @PostMapping("")
    public ResponseEntity<StatusDTO> createNewStatuses(@Valid @RequestBody StatusDTO status) {
        StatusDTO createdStatus = service.createNewStatuses(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateStatuses(@PathVariable Integer id, @RequestBody TaskStatus task) {
        return ResponseEntity.ok(service.updateStatuses(id, task));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removeStatuses(@Valid @PathVariable Integer id){
        return ResponseEntity.ok(service.removeStatuses(id));
    }
    @DeleteMapping("/{id}/{newId}")
    public ResponseEntity<Object> removeStatusAndReplace(@Valid @PathVariable Integer id , @PathVariable Integer newId){
        return ResponseEntity.ok(service.transferStatuses(id,newId));
    }

//    @PatchMapping("/{id}/maximum-task")

}
