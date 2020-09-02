package ro.licenta.backend.user;

public class User {

    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;

    public User(String firstname, String lastname,String username, String email) {
        super();
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;

    }

    public User(){
        this.id=id;
        this.firstname=firstname;
        this.lastname=lastname;
        this.username=username;
        this.email=email;
        this.password=password;
    }


    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }



}