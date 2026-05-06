import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { TicketService } from '../../../services/ticket.service';
import { AuthService } from '../../../services/auth.service';
import { Ticket, Comment } from '../../../models/ticket.model';

@Component({
  selector: 'app-admin-ticket-manage',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, NavbarComponent],
  templateUrl: './admin-ticket-manage.component.html',
  styleUrl: './admin-ticket-manage.component.css'
})
export class AdminTicketManageComponent implements OnInit {
  ticket = signal<Ticket | null>(null);
  comments = signal<Comment[]>([]);
  loading = signal(true);
  saving = signal(false);
  commentLoading = signal(false);
  error = signal('');
  success = signal('');
  newComment = '';

  // Edit fields
  editStatus = '';
  editPriority = '';

  statuses = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];
  priorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

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
          this.editStatus = response.data.status;
          this.editPriority = response.data.priority;
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

  updateTicket(): void {
    const ticketId = this.ticket()?.id;
    if (!ticketId) return;

    this.saving.set(true);
    this.success.set('');
    this.error.set('');

    this.ticketService.updateTicket(ticketId, {
      status: this.editStatus as any,
      priority: this.editPriority as any
    }).subscribe({
      next: (response) => {
        this.saving.set(false);
        if (response.success) {
          this.ticket.set(response.data);
          this.success.set('Ticket updated successfully');
          setTimeout(() => this.success.set(''), 3000);
        }
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err.error?.message || 'Failed to update ticket');
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
}
