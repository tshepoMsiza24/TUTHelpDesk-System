package com.HelpDeskSystem.demo.controller;

import com.HelpDeskSystem.demo.dto.request.TicketRequest;
import com.HelpDeskSystem.demo.dto.request.TicketUpdateRequest;
import com.HelpDeskSystem.demo.dto.response.TicketResponse;
import com.HelpDeskSystem.demo.service.TicketService;
import com.HelpDeskSystem.demo.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Tickets", description = "Ticket management endpoints")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    @Operation(summary = "Create a new ticket (Student)")
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(
            @Valid @RequestBody TicketRequest request,
            Authentication authentication) {
        TicketResponse ticket = ticketService.createTicket(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Ticket created successfully", ticket));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all tickets (Admin only)")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getAllTickets() {
        List<TicketResponse> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my tickets (Student)")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getMyTickets(Authentication authentication) {
        List<TicketResponse> tickets = ticketService.getMyTickets(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(
            @PathVariable Long id,
            Authentication authentication) {
        TicketResponse ticket = ticketService.getTicketById(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a ticket")
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicket(
            @PathVariable Long id,
            @RequestBody TicketUpdateRequest request,
            Authentication authentication) {
        TicketResponse ticket = ticketService.updateTicket(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Ticket updated successfully", ticket));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a ticket (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(ApiResponse.success("Ticket deleted successfully", null));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get tickets by status (Admin only)")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getTicketsByStatus(@PathVariable String status) {
        List<TicketResponse> tickets = ticketService.getTicketsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }
}
