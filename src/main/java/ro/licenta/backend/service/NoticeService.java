package ro.licenta.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ro.licenta.backend.data.Notice;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoticeService implements Serializable {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int store(Notice notice){
        try{
            return jdbcTemplate.update("INSERT into notice( name, notice_text) VALUE (?,?)",
            notice.getName(),notice.getText());
        }catch (Exception e){
            return 0;
        }
    }

    public List<Notice> findNotes(){
        try {
            return jdbcTemplate.query("SELECT name, notice_text FROM notice",
                    (rs,rowNum) -> new Notice(rs.getString("name"),rs.getString("notice_text")));

        } catch (Exception e) {
            return new ArrayList<Notice>();
        }
    }
    public int deleteNotice(Notice notice) {
        try {
            return jdbcTemplate.update("DELETE FROM notice WHERE name = ?",
                    notice.getName());
        } catch (Exception e) {
            return 0;
        }
    }
}
