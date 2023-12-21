package taskmanager.model.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import taskmanager.annotation.validator.list.ListValueNotNull;

@Getter
@Setter
@NoArgsConstructor
public class CreateTaskRequest {

    @NotBlank(message = "Title is required field")
    private String title;

    @NotBlank(message = "Description is required field")
    private String description;

    @Null
    private String status;

    @ListValueNotNull(allowedValues = {"Low", "Medium", "High", "Critical"} )
    private String priority;

    @Null
    private Long author;

    @NotNull(message = "ParentTaskId is required field")
    private Long responsiblePerson;
}
