package com.HelpDeskSystem.demo.service;

import com.HelpDeskSystem.demo.dto.request.CommentRequest;
import com.HelpDeskSystem.demo.dto.response.CommentResponse;
import com.HelpDeskSystem.demo.dto.response.UserResponse;
import com.HelpDeskSystem.demo.model.Comment;
import com.HelpDeskSystem.demo.model.Ticket;
import com.HelpDeskSystem.demo.model.User;
import com.HelpDeskSystem.demo.repository.CommentRepository;
import com.HelpDeskSystem.demo.repository.TicketRepository;
import com.HelpDeskSystem.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CommentResponse addComment(Long ticketId, CommentRequest request, String username) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Students can only comment on their own tickets
        if (user.getRole() == User.Role.STUDENT && !ticket.getStudent().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        Comment comment = Comment.builder()
                .ticket(ticket)
                .user(user)
                .comment(request.getComment())
                .build();

        comment = commentRepository.save(comment);
        return mapToResponse(comment);
    }

    public List<CommentResponse> getCommentsByTicketId(Long ticketId, String username) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Students can only view comments on their own tickets
        if (user.getRole() == User.Role.STUDENT && !ticket.getStudent().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only the comment author or admin can delete
        if (user.getRole() != User.Role.ADMIN && !comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .ticketId(comment.getTicket().getId())
                .user(mapUserToResponse(comment.getUser()))
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
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
}
