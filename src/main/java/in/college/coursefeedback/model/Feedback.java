
package in.college.coursefeedback.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String studentName;

    @Column(nullable = false, length = 120)
    private String courseName;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 2000)
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FeedbackStatus status = FeedbackStatus.SUBMITTED;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    // Required by JPA
    protected Feedback() {
    }

    // Constructor for submitting feedback
    public Feedback(String studentName, String courseName,
                    Integer rating, String comments) {
        this.studentName = studentName;
        this.courseName = courseName;
        this.rating = rating;
        this.comments = comments;
        this.status = FeedbackStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }

    @PrePersist
    void beforeInsert() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }

        if (status == null) {
            status = FeedbackStatus.SUBMITTED;
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }

    public FeedbackStatus getStatus() {
        return status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    // Setters for updating feedback
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }
}
