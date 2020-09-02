package ro.licenta.backend.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import ro.licenta.backend.user.User;

import javax.sql.DataSource;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

@Service
public class DBService implements Serializable {

    @Autowired
    private static JdbcTemplate jdbcTemplate;



    /* @Autowired
     private PasswordEncoder passwordEncoder;

     public static int insertUser(User user){
         try {
             return jdbcTemplate.update("INSERT INTO users(first_name, last_name, username, password, email) VALUES (?, ?, ?, ?, ?)",
                     user.getFirstname(),user.getLastname(), user.getUsername(), user.getPassword(passwordEncoder.encode(user.getPassword(passwordEncoder.encode(user.getPassword())))), user.getEmail());

         } catch (Exception e) {
             return 0;
         }

     }*/
    @Configuration
    public class DatabaseConfig {
        @Bean
        public DataSource dataSource(){
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/beehousedb");
            dataSource.setUsername( "root" );
            dataSource.setPassword( "root" );
            return dataSource;
        }
    }
}
