package co.com.report.model.auth;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
public class Auth {
    private String id;
    private String email;
    private String token;
    private LocalDateTime createdAt;
    private Integer expiresIn;
}
