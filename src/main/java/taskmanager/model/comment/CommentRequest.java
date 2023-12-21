package taskmanager.model.comment;

import jakarta.validation.constraints.NotBlank;
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
public class CommentRequest {

    @Null
    private Long id;

    @NotNull(message = "ParentTaskId is required field")
    private Long parentTaskId;

    @NotBlank(message = "Comment should contains text")
    private String text;

    @Null
    private Long author;
}
