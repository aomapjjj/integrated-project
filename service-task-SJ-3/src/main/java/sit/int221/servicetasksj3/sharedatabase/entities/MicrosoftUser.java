package sit.int221.servicetasksj3.sharedatabase.entities;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MicrosoftUser {
    private String oid;
    private String email;
    private String displayName;

}