
package in.college.coursefeedback.service;

import in.college.coursefeedback.model.Feedback;
import in.college.coursefeedback.model.FeedbackStatus;
import in.college.coursefeedback.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional
    public Feedback submit(String studentName, String courseName,
                           Integer rating, String comments) {
        Feedback feedback = new Feedback(
                studentName.trim(),
                courseName.trim(),
                rating,
                comments.trim()
        );

        return feedbackRepository.save(feedback);
    }

    @Transactional(readOnly = true)
    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Feedback findById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Feedback not found with ID: " + id));
    }

    @Transactional
    public Feedback update(Long id, String studentName, String courseName,
                           Integer rating, String comments) {
        Feedback feedback = findById(id);

        feedback.setStudentName(studentName.trim());
        feedback.setCourseName(courseName.trim());
        feedback.setRating(rating);
        feedback.setComments(comments.trim());

        return feedbackRepository.save(feedback);
    }

    @Transactional
    public void delete(Long id) {
        Feedback feedback = findById(id);
        feedbackRepository.delete(feedback);
    }

    @Transactional(readOnly = true)
    public List<Feedback> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        String searchTerm = keyword.trim();

        List<Feedback> results =
                feedbackRepository.findByStudentNameContainingIgnoreCase(searchTerm);

        List<Feedback> courseResults =
                feedbackRepository.findByCourseNameContainingIgnoreCase(searchTerm);

        for (Feedback feedback : courseResults) {
            if (results.stream().noneMatch(
                    existing -> existing.getId().equals(feedback.getId()))) {
                results.add(feedback);
            }
        }

        return results;
    }

    @Transactional
    public Feedback updateStatus(Long id, FeedbackStatus status) {
        Feedback feedback = findById(id);
        feedback.setStatus(status);
        return feedbackRepository.save(feedback);
    }
}
