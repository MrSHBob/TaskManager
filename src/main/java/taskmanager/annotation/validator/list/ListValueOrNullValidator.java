package taskmanager.annotation.validator.list;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ListValueOrNullValidator implements ConstraintValidator<ListValueOrNull, String> {

    private List<String> allowedValues;

    @Override
    public void initialize(ListValueOrNull constraintAnnotation) {
        allowedValues = List.of(constraintAnnotation.allowedValues());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || allowedValues.contains(value);
    }
}
