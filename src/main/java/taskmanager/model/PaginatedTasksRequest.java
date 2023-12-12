package taskmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaginatedTasksRequest {
    private Integer page;
    private Integer size;
    private Long authorFilter;
    private Long responsiblePersonFilter;
}
