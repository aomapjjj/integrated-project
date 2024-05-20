package sit.int221.servicetasksj3.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "statustasks")
public class TaskStatus {
    @Id
    @Column(name = "statusId", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "statusname", length = 50)
    private String name;
    @Column(name = "statusdescription", length = 200)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private List<Task> tasks = new ArrayList<>();

    @Column(name = "createdOn", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime createdOn;
    @Column(name = "updatedOn", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime updatedOn;

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


