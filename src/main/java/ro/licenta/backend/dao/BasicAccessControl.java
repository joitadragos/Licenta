package ro.licenta.backend.dao;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import ro.licenta.backend.user.User;

/**
 * Default mock implementation of {@link UserAuthenticationDAO}. This implementation
 * accepts any string as a user if the password is the same string, and
 * considers the user "admin" as the only administrator.
 */
public class BasicAccessControl implements UserAuthenticationDAO {


    public boolean signIn(String username, String password) {
        if (username.equals("admin") && password.equals("admin")) {
            CurrentUser.set(username);
            return true;
        }else{
            return false;
        }

    }
  /* public boolean signIn(User userRequest) {
       boolean result = false;
      String username = userRequest.getUsername();
      String password = userRequest.getPassword();
      if(username == "admin" && password == "admin"){
          CurrentUser.set(username);
          result = true;
      }else{
          if(checkAuthentication(userRequest)){
              CurrentUser.set(username);
              result = true;
          }else{
              result = false;
          }
      }
      return result;

   }*/


    @Override
    public boolean checkAuthentication(User userRequest) {
        return false;
    }

    @Override
    public boolean isUserSignedIn() {
        return !CurrentUser.get().isEmpty();
    }

    @Override
    public boolean isUserInRole(String role) {
        if ("admin".equals(role)) {
            // Only the "admin" user is in the "admin" role
            return getPrincipalName().equals("admin");
        }

        // All users are in all non-admin roles
        return true;


    }

    @Override
    public String getPrincipalName() {
        return CurrentUser.get();
    }

    @Override
    public void signOut() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().navigate("login");
    }
}