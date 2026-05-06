package com.HelpDeskSystem.demo.repository;

import com.HelpDeskSystem.demo.model.Ticket;
import com.HelpDeskSystem.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStudent(User student);
    List<Ticket> findByStudentId(Long studentId);
    List<Ticket> findByAssignedTo(User assignedTo);
    List<Ticket> findByStatus(Ticket.Status status);
    List<Ticket> findByPriority(Ticket.Priority priority);
    List<Ticket> findByCategoryId(Long categoryId);

    @Query("SELECT t FROM Ticket t ORDER BY t.createdAt DESC")
    List<Ticket> findAllOrderByCreatedAtDesc();

    @Query("SELECT t FROM Ticket t WHERE t.student.id = :studentId ORDER BY t.createdAt DESC")
    List<Ticket> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
