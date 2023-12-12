package taskmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import taskmanager.dao.Comment;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private LocalDateTime created;
    private LocalDateTime lastUpdateDate;
    private Integer version;
    private Long parentTaskId;
    private String text;
    private UserResponse author;

    public CommentResponse(Comment c) {
        this.id = c.getId();
        this.created = c.getCreated();
        this.lastUpdateDate = c.getLastUpdateDate();
        this.version = c.getVersion();
        this.parentTaskId = c.getParentTaskId();
        this.text = c.getText();
        this.author = new UserResponse(c.getAuthor());
    }
}
