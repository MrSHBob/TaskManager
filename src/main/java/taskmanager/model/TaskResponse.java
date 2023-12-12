package taskmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import taskmanager.dao.Task;

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
    private List<CommentResponse> comments;

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
        this.comments = new ArrayList<>();
        if ((t.getComments() != null) && (!t.getComments().isEmpty())) {
            t.getComments().forEach(comment -> {
                comments.add(new CommentResponse(comment));
            });
        }
    }
}
