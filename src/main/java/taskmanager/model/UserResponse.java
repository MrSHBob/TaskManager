package taskmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import taskmanager.dao.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private LocalDateTime created;
    private LocalDateTime lastUpdateDate;
    private Integer version;
    private String username;
    private String role;

    public UserResponse(User u) {
        this.id = u.getId();
        this.created = u.getCreated();
        this.lastUpdateDate = u.getLastUpdateDate();
        this.version = u.getVersion();
        this.username = u.getUsername();
        this.role = u.getRole();
    }
}
