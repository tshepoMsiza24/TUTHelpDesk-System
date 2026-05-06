package com.HelpDeskSystem.demo.dto.request;

import lombok.Data;

@Data
public class TicketUpdateRequest {
    private String title;
    private String description;
    private String status;
    private String priority;
    private Long categoryId;
    private Long assignedToId;
}
