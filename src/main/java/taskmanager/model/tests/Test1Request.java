package taskmanager.model.tests;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import taskmanager.annotation.validator.list.ListValueNotNull;
import taskmanager.annotation.validator.list.ListValueOrNull;

@Getter
@Setter
@NoArgsConstructor
@Data
public class Test1Request {

    @NotBlank(message = "username shouldn't be null or empty")
    private String username;

    @Email(message = "invalid email address")
    private String email;

    @Pattern(regexp = "^\\d{10}$",message = "invalid mobile number entered ")
    private String phone;

    @Min(1)
    @Max(99)
    private String number;

    @ListValueNotNull(allowedValues = {"New", "InProcess", "Done", "Canceled"})
    private String status;

    @ListValueOrNull(allowedValues = {"Low", "Medium", "High", "Critical"})
    private String priority;
}
