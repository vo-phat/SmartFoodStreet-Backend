package SmartFoodStreet_Backend.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { DateValidator.class })
public @interface DateConstraint {
    String message() default "Invalid Date";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
