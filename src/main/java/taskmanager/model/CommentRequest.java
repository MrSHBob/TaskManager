package taskmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequest {
    private Long id;
    private Long parentTaskId;
    private String text;
    private Long author;
}
