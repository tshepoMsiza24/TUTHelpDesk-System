package com.HelpDeskSystem.demo.service;

import com.HelpDeskSystem.demo.dto.request.TicketRequest;
import com.HelpDeskSystem.demo.dto.request.TicketUpdateRequest;
import com.HelpDeskSystem.demo.dto.response.CategoryResponse;
import com.HelpDeskSystem.demo.dto.response.TicketResponse;
import com.HelpDeskSystem.demo.dto.response.UserResponse;
import com.HelpDeskSystem.demo.model.Category;
import com.HelpDeskSystem.demo.model.Ticket;
import com.HelpDeskSystem.demo.model.User;
import com.HelpDeskSystem.demo.repository.CategoryRepository;
import com.HelpDeskSystem.demo.repository.TicketRepository;
import com.HelpDeskSystem.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public TicketResponse createTicket(TicketRequest request, String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(Ticket.Priority.valueOf(request.getPriority().toUpperCase()))
                .status(Ticket.Status.OPEN)
                .student(student)
                .build();

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            ticket.setCategory(category);
        }

        ticket = ticketRepository.save(ticket);
        return mapToResponse(ticket);
    }

    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAllOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getMyTickets(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ticketRepository.findByStudentIdOrderByCreatedAtDesc(student.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TicketResponse getTicketById(Long id, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Students can only view their own tickets
        if (user.getRole() == User.Role.STUDENT && !ticket.getStudent().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return mapToResponse(ticket);
    }

    @Transactional
    public TicketResponse updateTicket(Long id, TicketUpdateRequest request, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Students can only update their own tickets and limited fields
        if (user.getRole() == User.Role.STUDENT) {
            if (!ticket.getStudent().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied");
            }
            // Students can only update title and description
            if (request.getTitle() != null) ticket.setTitle(request.getTitle());
            if (request.getDescription() != null) ticket.setDescription(request.getDescription());
        } else {
            // Admins can update all fields
            if (request.getTitle() != null) ticket.setTitle(request.getTitle());
            if (request.getDescription() != null) ticket.setDescription(request.getDescription());
            if (request.getStatus() != null) {
                Ticket.Status newStatus = Ticket.Status.valueOf(request.getStatus().toUpperCase());
                ticket.setStatus(newStatus);
                if (newStatus == Ticket.Status.RESOLVED || newStatus == Ticket.Status.CLOSED) {
                    ticket.setResolvedAt(LocalDateTime.now());
                }
            }
            if (request.getPriority() != null) {
                ticket.setPriority(Ticket.Priority.valueOf(request.getPriority().toUpperCase()));
            }
            if (request.getCategoryId() != null) {
                Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                ticket.setCategory(category);
            }
            if (request.getAssignedToId() != null) {
                User assignedTo = userRepository.findById(request.getAssignedToId())
                        .orElseThrow(() -> new RuntimeException("Assigned user not found"));
                ticket.setAssignedTo(assignedTo);
            }
        }

        ticket = ticketRepository.save(ticket);
        return mapToResponse(ticket);
    }

    @Transactional
    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticketRepository.delete(ticket);
    }

    public List<TicketResponse> getTicketsByStatus(String status) {
        Ticket.Status ticketStatus = Ticket.Status.valueOf(status.toUpperCase());
        return ticketRepository.findByStatus(ticketStatus).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus().name())
                .priority(ticket.getPriority().name())
                .category(ticket.getCategory() != null ? mapCategoryToResponse(ticket.getCategory()) : null)
                .student(mapUserToResponse(ticket.getStudent()))
                .assignedTo(ticket.getAssignedTo() != null ? mapUserToResponse(ticket.getAssignedTo()) : null)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .commentCount(ticket.getComments() != null ? ticket.getComments().size() : 0)
                .build();
    }

    private UserResponse mapUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private CategoryResponse mapCategoryToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
