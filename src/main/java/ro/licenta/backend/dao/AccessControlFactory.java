package ro.licenta.backend.dao;

public class AccessControlFactory {
    private static final AccessControlFactory INSTANCE = new AccessControlFactory();
    private final UserAuthenticationDAO accessControl = new BasicAccessControl();

    private AccessControlFactory() {
    }

    public static AccessControlFactory getInstance() {
        return INSTANCE;
    }


    public UserAuthenticationDAO createAccessControl() {
        return accessControl;
    }
}