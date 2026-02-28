package com.jsdc.worktime.schedule_;

import lombok.Data;

import java.util.List;

@Data
public class Schedule {
    private String schedule_id;
    private String summary;
    private String description;
    private String start_time;
    private String end_time;
    private String location;
    private List<Attendee> attendees;
}
