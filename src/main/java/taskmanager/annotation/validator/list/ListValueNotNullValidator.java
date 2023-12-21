package taskmanager.annotation.validator.list;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class ListValueNotNullValidator implements ConstraintValidator<ListValueNotNull, String> {

    private List<String> allowedValues;

    @Override
    public void initialize(ListValueNotNull constraintAnnotation) {
        allowedValues = List.of(constraintAnnotation.allowedValues());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && allowedValues.contains(value);
    }
}
