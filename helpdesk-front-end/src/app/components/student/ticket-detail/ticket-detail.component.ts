import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { TicketService } from '../../../services/ticket.service';
import { AuthService } from '../../../services/auth.service';
import { Ticket, Comment } from '../../../models/ticket.model';

@Component({
  selector: 'app-ticket-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, NavbarComponent],
  templateUrl: './ticket-detail.component.html',
  styleUrl: './ticket-detail.component.css'
})
export class TicketDetailComponent implements OnInit {
  ticket = signal<Ticket | null>(null);
  comments = signal<Comment[]>([]);
  loading = signal(true);
  commentLoading = signal(false);
  error = signal('');
  newComment = '';

  constructor(
    private route: ActivatedRoute,
    private ticketService: TicketService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadTicket(id);
    this.loadComments(id);
  }

  loadTicket(id: number): void {
    this.loading.set(true);
    this.ticketService.getTicketById(id).subscribe({
      next: (response) => {
        this.loading.set(false);
        if (response.success) {
          this.ticket.set(response.data);
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Failed to load ticket');
      }
    });
  }

  loadComments(id: number): void {
    this.ticketService.getComments(id).subscribe({
      next: (response) => {
        if (response.success) {
          this.comments.set(response.data);
        }
      }
    });
  }

  submitComment(): void {
    if (!this.newComment.trim()) return;
    const ticketId = this.ticket()?.id;
    if (!ticketId) return;

    this.commentLoading.set(true);
    this.ticketService.addComment(ticketId, { comment: this.newComment }).subscribe({
      next: (response) => {
        this.commentLoading.set(false);
        if (response.success) {
          this.comments.update(c => [...c, response.data]);
          this.newComment = '';
        }
      },
      error: () => {
        this.commentLoading.set(false);
      }
    });
  }

  getStatusClass(status: string): string {
    return `badge badge-${status.toLowerCase()}`;
  }

  getPriorityClass(priority: string): string {
    return `badge badge-${priority.toLowerCase()}`;
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleString('en-ZA', {
      day: '2-digit', month: 'short', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  getBackLink(): string {
    return this.authService.isAdmin() ? '/admin/tickets' : '/student/tickets';
  }
}
