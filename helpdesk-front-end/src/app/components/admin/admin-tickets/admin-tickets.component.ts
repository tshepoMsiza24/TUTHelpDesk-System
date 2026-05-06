import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { TicketService } from '../../../services/ticket.service';
import { Ticket } from '../../../models/ticket.model';

@Component({
  selector: 'app-admin-tickets',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, NavbarComponent],
  templateUrl: './admin-tickets.component.html',
  styleUrl: './admin-tickets.component.css'
})
export class AdminTicketsComponent implements OnInit {
  tickets = signal<Ticket[]>([]);
  filteredTickets = signal<Ticket[]>([]);
  loading = signal(true);
  error = signal('');
  filterStatus = signal('ALL');
  filterPriority = signal('ALL');
  searchQuery = signal('');

  constructor(private ticketService: TicketService) {}

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
          this.applyFilters();
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Failed to load tickets');
      }
    });
  }

  setStatusFilter(status: string): void {
    this.filterStatus.set(status);
    this.applyFilters();
  }

  onSearch(query: string): void {
    this.searchQuery.set(query);
    this.applyFilters();
  }

  applyFilters(): void {
    let result = this.tickets();
    const status = this.filterStatus();
    const query = this.searchQuery().toLowerCase();

    if (status !== 'ALL') {
      result = result.filter(t => t.status === status);
    }

    if (query) {
      result = result.filter(t =>
        t.title.toLowerCase().includes(query) ||
        t.student.fullName.toLowerCase().includes(query) ||
        t.description.toLowerCase().includes(query)
      );
    }

    this.filteredTickets.set(result);
  }

  deleteTicket(id: number): void {
    if (!confirm('Are you sure you want to delete this ticket?')) return;

    this.ticketService.deleteTicket(id).subscribe({
      next: () => {
        this.tickets.update(tickets => tickets.filter(t => t.id !== id));
        this.applyFilters();
      },
      error: () => alert('Failed to delete ticket')
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
