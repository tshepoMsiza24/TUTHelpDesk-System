import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { TicketService } from '../../../services/ticket.service';
import { AuthService } from '../../../services/auth.service';
import { Ticket } from '../../../models/ticket.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  tickets = signal<Ticket[]>([]);
  loading = signal(true);
  error = signal('');

  get openCount() { return this.tickets().filter(t => t.status === 'OPEN').length; }
  get inProgressCount() { return this.tickets().filter(t => t.status === 'IN_PROGRESS').length; }
  get resolvedCount() { return this.tickets().filter(t => t.status === 'RESOLVED').length; }
  get urgentCount() { return this.tickets().filter(t => t.priority === 'URGENT').length; }
  get recentTickets() { return this.tickets().slice(0, 8); }

  constructor(
    private ticketService: TicketService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadTickets();
  }

  loadTickets(): void {
    this.loading.set(true);
    this.ticketService.getAllTickets().subscribe({
      next: (response) => {
        this.loading.set(false);
        if (response.success) {
          this.tickets.set(response.data);
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Failed to load tickets');
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
    return new Date(date).toLocaleDateString('en-ZA', {
      day: '2-digit', month: 'short', year: 'numeric'
    });
  }
}
