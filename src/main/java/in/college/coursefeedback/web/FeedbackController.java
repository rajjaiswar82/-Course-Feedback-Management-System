
package in.college.coursefeedback.web;

import in.college.coursefeedback.model.Feedback;
import in.college.coursefeedback.model.FeedbackStatus;
import in.college.coursefeedback.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String roleBasedHome(Authentication authentication) {
        
        // If no authentication (security disabled), show feedback list
        if (authentication == null) {
            return "redirect:/feedback/new";
        }

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        if (isAdmin) {
            return "redirect:/dashboard";
        }

        return "redirect:/feedback";
    }

    @GetMapping("/feedback")
    public String listFeedback(
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Feedback> feedbackList = feedbackService.search(keyword);

        model.addAttribute("feedbackList", feedbackList);
        model.addAttribute("keyword", keyword == null ? "" : keyword);

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

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Feedback submitted successfully."
        );

        return "redirect:/feedback";
    }

    @GetMapping("/feedback/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        Feedback feedback = feedbackService.findById(id);

        FeedbackForm form = new FeedbackForm();
        form.setStudentName(feedback.getStudentName());
        form.setCourseName(feedback.getCourseName());
        form.setRating(feedback.getRating());
        form.setComments(feedback.getComments());

        model.addAttribute("feedbackForm", form);
        model.addAttribute("feedbackId", id);

        return "feedback-edit";
    }

    @PostMapping("/feedback/update/{id}")
    public String updateFeedback(
            @PathVariable Long id,
            @Valid @ModelAttribute("feedbackForm") FeedbackForm feedbackForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("feedbackId", id);
            return "feedback-edit";
        }

        feedbackService.update(
                id,
                feedbackForm.getStudentName(),
                feedbackForm.getCourseName(),
                feedbackForm.getRating(),
                feedbackForm.getComments()
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Feedback updated successfully."
        );

        return "redirect:/feedback";
    }

    @PostMapping("/feedback/delete/{id}")
    public String deleteFeedback(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        feedbackService.delete(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Feedback deleted successfully."
        );

        return "redirect:/feedback";
    }

    @PostMapping("/feedback/status/{id}")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam FeedbackStatus status,
            RedirectAttributes redirectAttributes) {

        feedbackService.updateStatus(id, status);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Feedback status updated successfully."
        );

        return "redirect:/feedback";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        List<Feedback> feedbackList = feedbackService.findAll();

        long totalFeedback = feedbackList.size();

        long submittedCount = feedbackList.stream()
                .filter(f -> f.getStatus() == FeedbackStatus.SUBMITTED)
                .count();

        long reviewedCount = feedbackList.stream()
                .filter(f -> f.getStatus() == FeedbackStatus.REVIEWED)
                .count();

        double averageRating = feedbackList.stream()
                .filter(f -> f.getRating() != null)
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        model.addAttribute("totalFeedback", totalFeedback);
        model.addAttribute("submittedCount", submittedCount);
        model.addAttribute("reviewedCount", reviewedCount);
        model.addAttribute("averageRating", averageRating);

        return "dashboard";
    }
}
