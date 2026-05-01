package co.com.report.mongodb.auth;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "auths")
public class AuthEntity {
    @Id
    private String id;
    private String email;
    private String token;
    private LocalDateTime createdAt;
    private Integer expiresIn;
}
