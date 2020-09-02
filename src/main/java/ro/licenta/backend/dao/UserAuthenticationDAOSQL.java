package ro.licenta.backend.dao;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ro.licenta.backend.user.User;

import java.util.List;

@Repository
public class UserAuthenticationDAOSQL implements UserAuthenticationDAO {

    public static final String AUTHENTICATED_USER_NAME = "authenticatedUserName";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserAuthenticationDAOSQL(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean checkAuthentication(User userRequest) {
        boolean result = false;
        List<User> users = jdbcTemplate.query("" +
                        "SELECT * FROM users WHERE username = ?",
                new String[]{userRequest.getUsername()},
                (rs, rowNum) -> new User(rs.getString("username"),
                        rs.getString("password"))
        );

        if (users.size() != 0) {

            User user = users.get(0);
            CurrentUser.set(user.getUsername());
            result = true;
            VaadinSession.getCurrent().setAttribute(AUTHENTICATED_USER_NAME, user.getUsername());

        }
        return result;
    }
    @Override
    public boolean signIn(String username, String password) {
        return false;
    }

    @Override
    public boolean isUserSignedIn() {
        return !CurrentUser.get().isEmpty();
    }

    @Override
    public boolean isUserInRole(String role){
        if("admin".equals(role)){
            //only the "admin" user is in the "admin" role
            return getPrincipalName().equals("admin");
        }
        // All users are in non-admin roles
        return true;
    }

    @Override
    public String getPrincipalName(){return CurrentUser.get();}

    @Override
    public void signOut() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().navigate("login");
    }
}
