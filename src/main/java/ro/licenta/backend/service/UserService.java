package ro.licenta.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ro.licenta.backend.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//So I use this class to valitate data for the registration form

@Service
public class UserService implements Serializable {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    String encodedPassword;
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public int store(User user) {
        encodedPassword = passwordEncoder.encode(user.getPassword());// here we encrypt the password
        try {
            return jdbcTemplate.update("INSERT INTO users(first_name, last_name, username, password, email) VALUES (?, ?, ?, ?, ?)",
                    user.getFirstname(),user.getLastname(), user.getUsername(), encodedPassword, user.getEmail());

        } catch (Exception e) {
            return 0;
        }
    }

    // Getting data for a user
    public List<User> findUser(){
        try {
            return jdbcTemplate.query("SELECT first_name, last_name, username, email FROM users",
                    (rs, rowNum) -> new User(rs.getString("first_name"), rs.getString("last_name"),rs.getString("username") ,rs.getString("email")));
        } catch (Exception e) {
            return new ArrayList<User>();
        }
    }


    public List<User> findByUsername(String username){
        try{
            return jdbcTemplate.query("SELECT first_name, last_name, username, email FROM users WHERE username = ?",
                    new Object[]{username},
                    (rs, rowNum) -> new User(rs.getString("first_name"),rs.getString("last_name"),rs.getString("username"),rs.getString("email")));
        }catch(Exception e){
            return new ArrayList<User>();
        }
    }

    private int insertUser(User user){
        try{
            return jdbcTemplate.update("INSERT INTO users(first_name, last_name, username, email) VALUES(?,?,?,?)",
                    user.getFirstname(),user.getLastname(),user.getUsername(),user.getEmail());
        }catch(Exception e){
            return 0;
        }
    }
    public int saveUser(User user){
        List<User> users = this.findByUsername(user.getUsername());
        if(users.size() > 0){
            return updateUser(user);
        }else{
            return insertUser(user);
        }
    }
    private int updateUser(User user){
        try{
            return jdbcTemplate.update("UPDATE users SET first_name = ?, last_name = ?, username = ?, email = ? WHERE username = ?",
                    user.getFirstname(),user.getLastname(),user.getUsername(),user.getEmail());
        }catch (Exception e){
            return 0;
        }
    }




    public int deleteUser(User user) {
        try {
            return jdbcTemplate.update("DELETE FROM users WHERE username = ?",
                    user.getUsername());
        } catch (Exception e) {
            return 0;
        }
    }



    //isNumeric tests if  username is made only with numbers
    public static boolean isNumeric(String username) {
        if (username == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(username);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    //containsDigit see if there is any number in a string
    public final boolean containsDigit(String s) {
        boolean containsDigit = false;

        if (s != null && !s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (containsDigit = Character.isDigit(c)) {
                    break;
                }
            }
        }

        return containsDigit;
    }
    public static boolean Has_Special(String s){

        if(s.length()>=1){
            Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
            Matcher hasSpecial = special.matcher(s);

            return hasSpecial.find();
        }
        else
            return false;
    }


    public String validateFirstName(String firstname) {
        if (firstname == null) {
            return "Firstname can't be empty";
        }
        if (firstname.length() < 2) {
            return "Firstname can't be shorter than 2 characters";
        }
        if(containsDigit(firstname)==true){
            return "Firstanme can't have any numbers";
        }
        if(Has_Special(firstname)){
            return "Firstname can't have special characters";
        }
        return null;
    }
    public String validateLastName(String lastname) {
        if (lastname == null) {
            return "Lastname can't be empty";
        }
        if (lastname.length() < 2) {
            return "Lastname can't be shorter than 2 characters";
        }
        if(containsDigit(lastname)==true){
            return "Lastname can't have any numbers";
        }
        if(Has_Special(lastname)){
            return "Lastname can't have special characters";
        }
        return null;
    }

    public String validateUsername(String username) {

        if (username == null) {
            return "Username can't be empty";
        }
        if (username.length() < 4) {
            return "Username can't be shorter than 4 characters";
        }
        if(isNumeric(username)==true){
            return "Username can't be a number";
        }
        if(Has_Special(username)){
            return "Username can't have special characters";
        }

        List<String> reservedUsernames = Arrays.asList("admin", "test", "null", "void");
        if (reservedUsernames.contains(username)) {
            return String.format("'%s' is not available as a username", username);
        }

        return null;
    }

    public String validateLogin(String username, String password){
        if(username == null && password == null){
            return "Username and password can't be empty";
        }
        if(password == null){
            return "Introduce password";
        }
        return null;
    }

    /**
     * Utility Exception class that we can use in the frontend to show that
     * something went wrong during save.
     */
    public static class ServiceException extends Exception {
        public ServiceException(String msg) {
            super(msg);
        }
    }

}
