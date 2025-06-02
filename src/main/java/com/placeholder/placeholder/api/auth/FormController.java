package com.placeholder.placeholder.api.auth;

import com.placeholder.placeholder.api.auth.dto.RegistrationFormDto;
import com.placeholder.placeholder.api.auth.service.AuthService;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseUtils;
import com.placeholder.placeholder.api.util.common.messages.dto.error.ErrorCategory;
import com.placeholder.placeholder.api.util.common.messages.dto.error.details.ValidationErrorDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping()
@RequiredArgsConstructor
public class FormController {
    private final Logger log = LoggerFactory.getLogger(FormController.class);
    private final AuthService authService;

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }

    /**
     * Handles user registration.
     *
     * @param form    the registration form data
     * @param result  the binding result for validation errors. Also prevents the exception from being thrown and, instead,
     *                allows the method to show the user the errors in the view.
     * @param model   the model to add attributes for the view
     * @return the view name to render
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid RegistrationFormDto form, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            log.warn("Registration form has errors: {}", result.getAllErrors());
            List<ValidationErrorDetail> errorDetails = ApiResponseUtils.getErrorDetails(result, ErrorCategory.VALIDATION);

            for (ValidationErrorDetail errorDetail : errorDetails) {
                model.addAttribute("error", errorDetail.message());
            }

            return "register";
        }

        if (!form.password().equals(form.confirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Passwords do not match");
            return "register";
        }

        authService.registerUser(form);
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
        return "redirect:/login";
    }
}
