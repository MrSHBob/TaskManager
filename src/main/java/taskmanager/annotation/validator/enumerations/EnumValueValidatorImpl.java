package taskmanager.annotation.validator.enumerations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValueValidatorImpl implements ConstraintValidator<EnumValueValidator, String> {

    private EnumValueValidator annotation;

    @Override
    public void initialize(EnumValueValidator annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;  // null values are considered valid
        }

        Object[] enumValues = annotation.enumClass().getEnumConstants();

        for (Object enumValue : enumValues) {
            if (value.equals(enumValue.toString())) {
                return true;
            }
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        annotation.message().replace("{enumValues}", enumValuesAsString(enumValues)))
                .addConstraintViolation();

        return false;
    }

    private String enumValuesAsString(Object[] enumValues) {
        StringBuilder sb = new StringBuilder();
        for (Object enumValue : enumValues) {
            sb.append(enumValue.toString()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}
