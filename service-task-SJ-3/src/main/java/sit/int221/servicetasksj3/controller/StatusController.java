package sit.int221.servicetasksj3.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.servicetasksj3.dtos.limitsDTO.SimpleLimitDTO;
import sit.int221.servicetasksj3.dtos.statusesDTO.StatusDTO;
import sit.int221.servicetasksj3.dtos.statusesDTO.StatusDTOTwo;
import sit.int221.servicetasksj3.entities.TaskLimit;
import sit.int221.servicetasksj3.entities.TaskStatus;
import sit.int221.servicetasksj3.services.StatusService;

import java.util.List;

@RestController
@RequestMapping("/statuses")
@CrossOrigin(origins = { "http://localhost:5173/", "http://ip23sj3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th" } )
public class StatusController {
    @Autowired
    private StatusService service;

    @GetMapping("")
    public List<StatusDTOTwo> getAllStatuses() {
        return service.getAllStatuses();
    }

    @GetMapping("/{id}")
    public TaskStatus getStatusesById(@PathVariable Integer id){
        return service.getStatusesById(id);
    }

    @GetMapping("/limit")
    public ResponseEntity<TaskLimit> getStatusesLimit(){
        return ResponseEntity.ok(service.getStatusesLimit());
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

    @PatchMapping("/maximumtask")
    public ResponseEntity<SimpleLimitDTO> updateLimitTask(
            @RequestParam @Min(0) @Max(30) Integer maximumTask,
            @RequestParam Boolean isLimit) {
        return ResponseEntity.ok(service.updateLimitTask(maximumTask, isLimit));
    }
}
