package ro.licenta.views.admin;

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
import ro.licenta.backend.service.UserService;
import ro.licenta.backend.user.User;
import ro.licenta.views.MainView;

@Route(value = "master-detail", layout = MainView.class)
@RouteAlias(value = "master-details", layout = MainView.class)
@PageTitle("Master-Detail")

public class MasterDetailView extends Div implements AfterNavigationObserver{
    public static final String VIEW_NAME = "User-Details";
    @Autowired
    private UserService userService;

    private Grid<User> users;

    private TextField firstname = new TextField();
    private TextField lastname = new TextField();
    private TextField username = new TextField();
    private TextField email = new TextField();
    private PasswordField password = new PasswordField();
    private IntegerField beehive = new IntegerField();

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private Binder<User> binder;

    public MasterDetailView() {
        setId("user-detail-view");

        //Configure Grid

        users = new Grid<>();
        users.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        users.setHeightFull();
        users.addColumn(User::getFirstname).setHeader("First Name").setFlexGrow(7);
        users.addColumn(User::getLastname).setHeader("Last Name");
        users.addColumn(User::getUsername).setHeader("Username");
        users.addColumn(User::getEmail).setHeader("Email");

        //when a row is selected or deselected, populate form
        users.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        // Configure Form
        binder = new Binder<>(User.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);
        // note that password field isn't bound since that property doesn't exist in
        // Employee

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

        delete.addClickListener(e -> {
            //Aici e smecheria la baza de date
            // revin aici si schimbam unde e nevoie si ce e nevoie
            User user = binder.getBean();
            if(userService.deleteUser(user) > 0){
                users.setItems(userService.findUser());
            }else{
                Notification.show("Delete error");
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
        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, firstname, "First name");
        addFormItem(editorDiv, formLayout, lastname, "Last name");
        addFormItem(editorDiv, formLayout, username, "Username");
        addFormItem(editorDiv, formLayout, email, "Email");
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
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(delete, cancel, save);
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

        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        users.setItems(userService.findUser());

    }

    private void populateForm(User value) {
        // Value can be null as well, that clears the form

        if ( value == null ) {
            value = new User();
        }
        binder.setBean(value);

    }
}