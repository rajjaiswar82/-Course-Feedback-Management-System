package in.college.coursefeedback.web;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackFormValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @AfterAll
    static void tearDown() {
        Validation.buildDefaultValidatorFactory().close();
    }

    @Test
    void validFormHasNoConstraintViolations() {
        FeedbackForm form = new FeedbackForm();
        form.setStudentName("Raj");
        form.setCourseName("Database Management Systems");
        form.setRating(5);
        form.setComments("The course was clear and useful.");

        assertTrue(validator.validate(form).isEmpty());
    }

    @Test
    void ratingOutsideRangeIsRejected() {
        FeedbackForm form = new FeedbackForm();
        form.setStudentName("Raj");
        form.setCourseName("DBMS");
        form.setRating(6);
        form.setComments("Helpful course.");

        assertFalse(validator.validate(form).isEmpty());
    }

    @Test
    void blankRequiredFieldsAreRejected() {
        FeedbackForm form = new FeedbackForm();
        form.setStudentName(" ");
        form.setCourseName("");
        form.setRating(null);
        form.setComments(" ");

        assertTrue(validator.validate(form).size() >= 4);
    }
}
