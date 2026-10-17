
package in.college.coursefeedback.repository;

import in.college.coursefeedback.model.Feedback;
import in.college.coursefeedback.model.FeedbackStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Search feedback by student name, ignoring letter case
    List<Feedback> findByStudentNameContainingIgnoreCase(String studentName);

    // Search feedback by course name, ignoring letter case
    List<Feedback> findByCourseNameContainingIgnoreCase(String courseName);

    // Filter feedback by status
    List<Feedback> findByStatus(FeedbackStatus status);
}
