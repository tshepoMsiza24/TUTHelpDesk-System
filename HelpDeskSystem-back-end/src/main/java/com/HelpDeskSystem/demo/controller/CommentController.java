package com.HelpDeskSystem.demo.controller;

import com.HelpDeskSystem.demo.dto.request.CommentRequest;
import com.HelpDeskSystem.demo.dto.response.CommentResponse;
import com.HelpDeskSystem.demo.service.CommentService;
import com.HelpDeskSystem.demo.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Comments", description = "Comment management endpoints")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    @Operation(summary = "Add a comment to a ticket")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        CommentResponse comment = commentService.addComment(ticketId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Comment added successfully", comment));
    }

    @GetMapping
    @Operation(summary = "Get all comments for a ticket")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable Long ticketId,
            Authentication authentication) {
        List<CommentResponse> comments = commentService.getCommentsByTicketId(ticketId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long ticketId,
            @PathVariable Long commentId,
            Authentication authentication) {
        commentService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }
}
