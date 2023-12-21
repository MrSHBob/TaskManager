package taskmanager.annotation.validator.list;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListValueNotNullValidator.class)
public @interface ListValueNotNull {

    String message() default "Invalid value. Allowed values are: {allowedValues}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] allowedValues();
}
