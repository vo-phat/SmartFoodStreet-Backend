package SmartFoodStreet_Backend.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DateValidator implements ConstraintValidator<DateConstraint, LocalDate> {
    private int min;

    @Override
    public void initialize(DateConstraint dateConstraint) {
        min = dateConstraint.min();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(localDate))
            return true;

        long years = ChronoUnit.YEARS.between(localDate, LocalDate.now());

        return years >= min;
    }
}
    // Cách dùng: @DateConstraint(min = 18, message = "INVALID_DATE")
