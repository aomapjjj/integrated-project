package sit.int221.servicetasksj3.sharedatabase.dtos;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseTokenDTO {
    private String access_token;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refresh_token;
}
