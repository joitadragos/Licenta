package ro.licenta.views;


import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;



@Route("")
@PageTitle("About")
public class AboutView extends VerticalLayout {
    private Button loginButton ;
    private Button registerButton;
    public AboutView(){
        setSpacing(true);
        setPadding(true);
        add(new H2("About View For BeeHouseApp"));

        add(new Text("This is the first page, from here is your decision what do you want to do"));

        category("How to use");
        add(new Text("It's easy, first create you account, login and see what's next"));

        Button registerButton = new Button("Register",new Icon(VaadinIcon.HANDS_UP));
        Button loginButton = new Button("Login",new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));

        HorizontalLayout layout = createLayout("");
        layout.add(registerButton, loginButton);


        layout.setPadding(true);
        layout.addAndExpand(registerButton,loginButton); // adds and flex-grows both components
        layout.setAlignItems(Alignment.CENTER);

        registerButton.addClickListener( e -> getUI().get().navigate("registration"));
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        loginButton.addClickListener( e -> getUI().get().navigate("login"));
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }
    private void category(String string) {
        add(new H3(string));
    }


    private HorizontalLayout createLayout(String caption) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidthFull();
        hl.getStyle().set("background-color", "#dddddd");
        add(new Text(caption));
        add(hl);
        add(new Html("<span>&nbsp;</span>")); // spacer
        return hl;
    }
}
