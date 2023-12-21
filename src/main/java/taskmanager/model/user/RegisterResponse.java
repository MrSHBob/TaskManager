package taskmanager.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import taskmanager.dao.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RegisterResponse {
    private Long id;
    private String username;
    private String role;

    public RegisterResponse(User u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.role = u.getRole();
    }
}
