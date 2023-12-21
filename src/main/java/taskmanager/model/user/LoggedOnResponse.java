package taskmanager.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import taskmanager.dao.User;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class LoggedOnResponse {
    private String token;
}
