package ro.licenta.views.registration;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ro.licenta.backend.service.UserService;
import ro.licenta.backend.user.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the default (and only) view in this example.
 * <p>
 * It demonstrates how to create a form using Vaadin and the Binder. The backend
 * service and data class are in the <code>.data</code> package.
 */
@Route("registration")
@PageTitle("Registration")

public class RegistrationView extends VerticalLayout {

    private PasswordField passwordField1;
    private PasswordField passwordField2;

    private UserService service;
    private BeanValidationBinder<User> binder;

    /**
     * Flag for disabling first run for password validation
     */
    private boolean enablePasswordValidation;

    /**
     * We use Spring to inject the backend into our view
     */

    public RegistrationView(@Autowired UserService service) {

        this.service = service;

        /*
         * Create the components we'll need
         */

        H3 title = new H3("Bee House Registration");

        TextField firstnameField = new TextField("First name");
        TextField lastnameField = new TextField("Last name");
        TextField usernameField = new TextField("Username");
        EmailField emailField = new EmailField("Email");
        emailField.setVisible(true);
        passwordField1 = new PasswordField("Wanted password");
        passwordField2 = new PasswordField("Password again");

        Span errorMessage = new Span();

        Button submitButton = new Button("Join now",new Icon(VaadinIcon.CHECK_CIRCLE));
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button loginButton = new Button("Login",new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));
        loginButton.addClickListener( e -> getUI().get().navigate("login"));

        Button cancelButton = new Button("Cancel",new Icon(VaadinIcon.EXIT));
        cancelButton.addClickListener(e -> getUI().get().navigate(""));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);


        /*
         * Build the visible layout
         */

        // Create a FormLayout with all our components. The FormLayout doesn't have any
        // logic (validation, etc.), but it allows us to configure Responsiveness from
        // Java code and its defaults looks nicer than just using a VerticalLayout.
        FormLayout formLayout = new FormLayout(title, firstnameField, lastnameField, usernameField, passwordField1, passwordField2,
                emailField, errorMessage, submitButton, loginButton,cancelButton);

        // Restrict maximum width and center on page
        formLayout.setMaxWidth("500px");
        formLayout.getStyle().set("margin", "0 auto");

        // Allow the form layout to be responsive. On device widths 0-490px we have one
        // column, then we have two. Field labels are always on top of the fields.
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("490px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        // These components take full width regardless if we use one column or two (it
        // just looks better that way)
        formLayout.setColspan(title, 2);
        formLayout.setColspan(errorMessage, 2);
        formLayout.setColspan(submitButton, 2);

        // Add some styles to the error message to make it pop out
        errorMessage.getStyle().set("color", "var(--lumo-error-text-color)");
        errorMessage.getStyle().set("padding", "15px 0");

        // Add the form to the page
        add(formLayout);

        /*
         * Set up form functionality
         */

        /*
         * Binder is a form utility class provided by Vaadin. Here, we use a specialized
         * version to gain access to automatic Bean Validation (JSR-303). We provide our
         * data class so that the Binder can read the validation definitions on that
         * class and create appropriate validators. The BeanValidationBinder can
         * automatically validate all JSR-303 definitions, meaning we can concentrate on
         * custom things such as the passwords in this class.
         */
        binder = new BeanValidationBinder<User>(User.class);

        // Basic name fields that are required to fill in
        binder.forField(firstnameField).withValidator(this::validateFirstname).asRequired().bind("firstname");
        binder.forField(lastnameField).withValidator(this::validateLastname).asRequired().bind("lastname");

        // The handle has a custom validator, in addition to being required. Some values
        // are not allowed, such as 'admin'; this is checked in the validator.
        binder.forField(usernameField).withValidator(this::validateUsername).asRequired().bind("username");

        binder.forField(emailField).asRequired(new VisibilityEmailValidator("Value is not a valid email address")).bind("email");


        // Another custom validator, this time for passwords
        binder.forField(passwordField1).asRequired().withValidator(this::passwordValidator).bind("password");

        passwordField2.addValueChangeListener(e -> {

            // The user has modified the second field, now we can validate and show errors.
            // See passwordValidator() for how this flag is used.
            enablePasswordValidation = true;

            binder.validate();
        });

        // A label where bean-level error messages go
        binder.setStatusLabel(errorMessage);

        // And finally the submit button
        submitButton.addClickListener(e -> {
            try {

                // Create empty bean to store the details into
                User detailsBean = new User();

                // Run validators and write the values to the bean
                binder.writeBean(detailsBean);

                // Call backend to store the data
                service.store(detailsBean);

                // Show success message if everything went well
                showSuccess(detailsBean);
                //Move to the login page
                getUI().get().navigate("login");
            } catch (ValidationException e1) {
                // validation errors are already visible for each field,
                // and bean-level errors are shown in the status label.

                // We could show additional messages here if we want, do logging, etc.

            }/* catch (ServiceException e2) {

                // For some reason, the save failed in the back end.

                // First, make sure we store the error in the server logs (preferably using a
                // logging framework)
                e2.printStackTrace();

                // Notify, and let the user try again.
                errorMessage.setText("Saving the data failed, please try again");
            } catch (com.vaadin.flow.server.ServiceException serviceException) {
                serviceException.printStackTrace();
            }*/
        });

    }



    /**
     * We call this method when form submission has succeeded
     */
    private void showSuccess(User detailsBean) {
        Notification notification = Notification.show("Data saved, welcome " + detailsBean.getUsername());
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        // Here you'd typically redirect the user to another view
    }

    /**
     * Method to validate that:
     * <p>
     * 1) Password is at least 8 characters long
     * <p>
     * 2) Values in both fields match each other
     */

    public static boolean Password_Validation(String password)
    {

        if(password.length()>=8)
        {
            Pattern letter = Pattern.compile("[a-zA-z]");
            Pattern digit = Pattern.compile("[0-9]");
            Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

            Matcher hasLetter = letter.matcher(password);
            Matcher hasDigit = digit.matcher(password);
            Matcher hasSpecial = special.matcher(password);

            return hasLetter.find() && hasDigit.find() && hasSpecial.find();

        }
        else
            return false;

    }
    private ValidationResult passwordValidator(String pass1, ValueContext ctx) {

        /*
         * Here we check if the password is good enaugh
         */
        if (pass1 == null || pass1.length() < 8) {
            return ValidationResult.error("Password should be at least 8 characters long");
        }
        if(Password_Validation(pass1)==false){
            return ValidationResult.error("Password should have letters, numbers and special caracters||||1,2,...,9|||@,!,#,$");
        }


        if (!enablePasswordValidation) {
            // user hasn't visited the field yet, so don't validate just yet, but next time.
            enablePasswordValidation = true;
            return ValidationResult.ok();
        }

        String pass2 = passwordField2.getValue();

        if (pass1 != null && pass1.equals(pass2)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error("Passwords do not match");
    }

    /**
     * Method that demonstrates using an external validator. Here we ask the backend
     * if this handle is already in use.
     */
    private ValidationResult validateUsername(String username, ValueContext ctx) {

        String errorMsg = service.validateUsername(username);

        if (errorMsg == null) {
            return ValidationResult.ok();
        }

        return ValidationResult.error(errorMsg);
    }
    private ValidationResult validateFirstname(String firstname, ValueContext ctx) {
        String errorMsg = service.validateFirstName(firstname);
        if (errorMsg == null) {
            return ValidationResult.ok();
        }

        return ValidationResult.error(errorMsg);
    }
    private ValidationResult validateLastname(String lastname, ValueContext ctx) {
        String errorMsg = service.validateLastName(lastname);
        if (errorMsg == null) {
            return ValidationResult.ok();
        }

        return ValidationResult.error(errorMsg);
    }


    public class VisibilityEmailValidator extends EmailValidator {

        public VisibilityEmailValidator(String errorMessage) {
            super(errorMessage);
        }

        @Override
        public ValidationResult apply(String value, ValueContext context) {
            // normal email validation
            return super.apply(value, context);
        }
    }
}
