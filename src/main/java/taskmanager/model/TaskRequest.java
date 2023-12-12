package taskmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskRequest {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Long author;
    private Long responsiblePerson;
}
