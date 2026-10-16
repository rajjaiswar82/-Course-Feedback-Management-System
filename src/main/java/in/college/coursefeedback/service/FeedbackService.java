package in.college.coursefeedback.service;

import in.college.coursefeedback.model.Feedback;
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
    public Feedback submit(String studentName, String courseName, Integer rating, String comments) {
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
}
