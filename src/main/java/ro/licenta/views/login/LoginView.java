package ro.licenta.views.login;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import ro.licenta.backend.dao.BasicAccessControl;
import ro.licenta.backend.dao.UserAuthenticationDAO;
import ro.licenta.backend.service.UserService;
import ro.licenta.backend.user.User;

@Route("login")
@RouteAlias("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeLeaveListener {

    private UserAuthenticationDAO userAuthenticationDAO;

    private Binder<User> userBinder = new Binder<>();
    private User user = new User("","");

    private TextField usernameTextField;
    private PasswordField passwordTextField;

    @Autowired
    public void setUserAuthenticationDAO(UserAuthenticationDAO userAuthenticationDAO){
        this.userAuthenticationDAO = userAuthenticationDAO;
    }

    public LoginView(){

        H3 title = new H3("Bee House Login");
        Span errorMessage = new Span();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.setHorizontalComponentAlignment(Alignment.CENTER);
        layout.setMaxWidth("500px");
        layout.getStyle().set("margin", "0 auto");


        usernameTextField = new TextField("username");
        passwordTextField = new PasswordField("Password");

        Button submit = new Button("Submit",new Icon(VaadinIcon.ENTER));
        Button cancel = new Button("Cancel",new Icon(VaadinIcon.EXIT));
        Button register = new Button("Register",new Icon(VaadinIcon.HANDS_UP));

        userBinder.bind(usernameTextField, User::getUsername, User::setUsername);
        userBinder.bind(passwordTextField, User::getPassword, User::setPassword);
        userBinder.setBean(user);



        //The register button redirect user to the registration page
        register.addClickListener( e -> getUI().get().navigate("registration"));
        register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        submit.addClickListener(e -> signIn(user));
        submit.addClickShortcut(Key.ENTER);

        cancel.addClickListener( e -> getUI().get().navigate(""));
        cancel.addClickShortcut(Key.ESCAPE);
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

       layout.add(title,usernameTextField,passwordTextField,submit,cancel,register);
       add(layout);


    }



    private void signIn(User userRequest) {
        if(userAuthenticationDAO.checkAuthentication(userRequest)){
            getUI().get().navigate("inventory");
            if(userRequest.getUsername().equals("admin")){
                Notification.show("HAHAHAHAHA");}
            Notification notification = Notification.show("Welcome " + userRequest.getUsername());
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }else{
            Notification.show("Invalid username or password");
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        usernameTextField.setValue("");
        passwordTextField.setValue("");
    }
}