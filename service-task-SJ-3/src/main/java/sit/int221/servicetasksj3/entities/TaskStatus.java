package sit.int221.servicetasksj3.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
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
    @OneToMany(mappedBy = "statusTasks")
    private Set<Task> tasks = new LinkedHashSet<>();

    public void setName(String name) {
        if (name != null) {
            name = name.trim();
        }
        this.name = name;
    }

    public void setDescription(String description) {
        if (description != null) {
            description = description.trim();
        }
        this.description = description;
    }
}


