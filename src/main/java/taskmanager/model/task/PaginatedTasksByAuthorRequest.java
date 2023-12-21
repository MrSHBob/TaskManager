package taskmanager.model.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaginatedTasksByAuthorRequest {

    @NotNull
    private Integer page;

    @NotNull
    private Integer size;

    @NotNull
    private Long authorFilter;
//    private Long responsiblePersonFilter;
}
