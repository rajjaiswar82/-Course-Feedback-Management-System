package in.college.coursefeedback.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FeedbackForm {
    @NotBlank(message = "Student name is required.")
    @Size(max = 100, message = "Student name must be at most 100 characters.")
    private String studentName;

    @NotBlank(message = "Course name is required.")
    @Size(max = 120, message = "Course name must be at most 120 characters.")
    private String courseName;

    @NotNull(message = "Please select a rating.")
    @Min(value = 1, message = "Rating must be between 1 and 5.")
    @Max(value = 5, message = "Rating must be between 1 and 5.")
    private Integer rating;

    @NotBlank(message = "Comments are required.")
    @Size(max = 2000, message = "Comments must be at most 2000 characters.")
    private String comments;

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
