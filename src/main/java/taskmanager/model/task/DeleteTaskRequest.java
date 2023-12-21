package taskmanager.model.task;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteTaskRequest {

    @NotNull(message = "ID is required field")
    private Long id;

    @Null
    private Long author;
}
