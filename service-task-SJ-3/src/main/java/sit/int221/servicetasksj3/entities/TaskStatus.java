package sit.int221.servicetasksj3.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "statustasks")
public class TaskStatus {
    @Id
    @Column(name = "statusid", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "statusname", length = 50)
    private String name;
    @Column(name = "statusdescription", length = 200)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "statusTasks", cascade = CascadeType.ALL)
    private Set<Task> tasks = new LinkedHashSet<>();
}


