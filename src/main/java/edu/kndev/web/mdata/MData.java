package edu.kndev.web.mdata;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Data
@Table(name="c_task")
@Entity // This tells Hibernate to make a table out of this class
public class MData {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String id;
    private String begin_time;
    private Timestamp create_date;
    private String ds_id;
    private int end_month;
    private String end_time;
    private int end_year;
    private int exec_status;
    private Timestamp  modify_date;
    private int order_num;
    private String searches;
    private int start_month;
    private int start_year;
    private int status;
    private boolean tested;
    @Column(name="topic_id")
    private String topicID;
    private String crawler_status;
    private int total_count;
    private String index_crawler_status;
    private String wosdatabase;
//此处省略get和set
}
