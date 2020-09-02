package ro.licenta;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import ro.licenta.backend.dao.AccessControlFactory;
import ro.licenta.backend.dao.UserAuthenticationDAO;
import ro.licenta.views.about.AboutView;
import ro.licenta.views.login.LoginView;
import ro.licenta.views.registration.RegistrationView;
/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
/*public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }

}*/
public class Application implements VaadinServiceInitListener{
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
    @Override
    public void serviceInit(ServiceInitEvent initEvent){
        final UserAuthenticationDAO accessControl = AccessControlFactory.getInstance()
                .createAccessControl();
       initEvent.getSource().addUIInitListener(uiInitEvent -> {
            uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {
                if (!accessControl.isUserSignedIn() && !LoginView.class.equals(enterEvent.getNavigationTarget())){
                    enterEvent.rerouteTo(LoginView.class);
                    enterEvent.rerouteTo(AboutView.class);

                    if(!AboutView.class.equals(enterEvent.getNavigationTarget())){
                        enterEvent.rerouteTo(RegistrationView.class);
                    }
                }

            });
        });
    }
}
