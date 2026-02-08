package jmaster.io.statisticservice.model;

import lombok.Data;

import java.util.Date;

@Data
public class StatisticDTO {
    private Long id;
    private String message;
    private Date createdDate;
}
