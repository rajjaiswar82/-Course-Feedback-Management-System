package in.college.coursefeedback.web;

import in.college.coursefeedback.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/feedback";
    }

    @GetMapping("/feedback")
    public String listFeedback(Model model) {
        model.addAttribute("feedbackList", feedbackService.findAll());
        return "feedback-list";
    }

    @GetMapping("/feedback/new")
    public String showForm(Model model) {
        if (!model.containsAttribute("feedbackForm")) {
            model.addAttribute("feedbackForm", new FeedbackForm());
        }
        return "feedback-form";
    }

    @PostMapping("/feedback")
    public String submitFeedback(
            @Valid @ModelAttribute("feedbackForm") FeedbackForm feedbackForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "feedback-form";
        }

        feedbackService.submit(
                feedbackForm.getStudentName(),
                feedbackForm.getCourseName(),
                feedbackForm.getRating(),
                feedbackForm.getComments()
        );
        redirectAttributes.addFlashAttribute("successMessage", "Feedback submitted successfully.");
        return "redirect:/feedback";
    }
}
