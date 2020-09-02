package ro.licenta.backend.dao;

//This interface specifies contracts for an implementing class resposible for user authentication


import ro.licenta.backend.user.User;

public interface UserAuthenticationDAO {
    //Checks user credentials
    boolean checkAuthentication(User userRequest);

    boolean signIn(String username, String password);

    String ADMIN_ROLE_NAME = "admin";

    boolean isUserSignedIn();

    boolean isUserInRole(String role);

    String getPrincipalName();

    //Manages sign-out
    void signOut();
}
