package ro.licenta.views.user;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import ro.licenta.backend.dao.CurrentUser;
import ro.licenta.backend.service.UserService;
import ro.licenta.backend.user.User;
import ro.licenta.views.main.MainView;

@Route(value = "user-detail", layout = MainView.class)
@RouteAlias(value = "user-details", layout = MainView.class)
@PageTitle("User-Details")
public class UserDetailView extends Div implements AfterNavigationObserver {
    public static final String VIEW_NAME = "User-Details";
    @Autowired
    private UserService userService;

    private Grid<User> users;

    private TextField firstname = new TextField();
    private TextField lastname = new TextField();
    private TextField username = new TextField();
    private TextField email = new TextField();

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");
    
    private Binder<User> binder;
    
    public UserDetailView() {
        setId("user-detail-view");
        
        //Configure Grid
        
        users = new Grid<>();
        users.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        users.setHeightFull();
        users.setWidthFull();
        users.addColumn(User::getFirstname).setHeader("First Name");
        users.addColumn(User::getLastname).setHeader("Last Name");
        users.addColumn(User::getUsername).setHeader("Username");
        users.addColumn(User::getEmail).setHeader("Email");

        //when a row is selected or deselected, populate form
        users.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        // Configure Form
        binder = new Binder<>(User.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        // the grid valueChangeEvent will clear the form too
        cancel.addClickListener(e -> users.asSingleSelect().clear());

        save.addClickListener(e -> {
            //Aici e smecheria la baza de date
            // revin aici si schimbam unde e nevoie si ce e nevoie
           User user = binder.getBean();
           if(userService.saveUser(user) > 0){
               users.setItems(userService.findUser());
           }else{
               Notification.show("Save error");
           }
        });

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorDiv = new Div();
        editorDiv.setId("editor-layout");
        editorDiv.setTitle("Change User Details");
        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, email, "Email");
        addFormItem(editorDiv, formLayout, username, "Username");

        createButtonLayout(editorDiv);
        splitLayout.addToSecondary(editorDiv);

    }

    private void createButtonLayout(Div editorDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(cancel, save);
        editorDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();

        splitLayout.addToPrimary(wrapper);
        wrapper.add(users);
    }

    private void addFormItem(Div wrapper, FormLayout formLayout,
                             AbstractField field, String fieldName) {
        formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
    }
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        String curentUser = CurrentUser.get();
        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
       users.setItems(userService.findByUsername(curentUser));
       // users.setItems((userService.findOneUser()));
    }

    private void populateForm(User value) {
        // Value can be null as well, that clears the form

        //binder.readBean(value); // commented out
        if ( value == null ) {
            value = new User();
        }
        binder.setBean(value);
    }
}
