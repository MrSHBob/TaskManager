package taskmanager.model.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import taskmanager.dao.Task;
import taskmanager.model.UserResponse;
import taskmanager.model.comment.CommentResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private UserResponse author;
    private UserResponse responsiblePerson;
    private LocalDateTime created;
    private LocalDateTime lastUpdateDate;

    public TaskResponse(Task t) {
        this.id = t.getId();
        this.created = t.getCreated();
        this.lastUpdateDate = t.getLastUpdateDate();
        this.title = t.getTitle();
        this.description = t.getDescription();
        this.status = t.getStatus();
        this.priority = t.getPriority();
        this.author = new UserResponse(t.getAuthor());
        this.responsiblePerson = new UserResponse(t.getResponsiblePerson());
    }
}
