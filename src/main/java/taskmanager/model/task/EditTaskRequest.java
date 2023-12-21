package taskmanager.model.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import taskmanager.annotation.validator.list.ListValueNotNull;

@Getter
@Setter
@NoArgsConstructor
public class EditTaskRequest {

    @NotNull(message = "ID is required field")
    private Long id;

    @NotBlank(message = "Title is required field")
    private String title;

    @NotBlank(message = "Description is required field")
    private String description;

    @ListValueNotNull(allowedValues = {"New", "InProcess", "Done", "Canceled"})
    private String status;

    @ListValueNotNull(allowedValues = {"Low", "Medium", "High", "Critical"} )
    private String priority;

    @Null
    private Long author;

    @NotNull(message = "ParentTaskId is required field")
    private Long responsiblePerson;
}
