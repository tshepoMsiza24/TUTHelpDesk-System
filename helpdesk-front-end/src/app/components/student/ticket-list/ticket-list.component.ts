import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { TicketService } from '../../../services/ticket.service';
import { Ticket } from '../../../models/ticket.model';
import { TicketFilterPipe } from '../../../pipes/ticket-filter.pipe';

@Component({
  selector: 'app-ticket-list',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent, TicketFilterPipe],
  templateUrl: './ticket-list.component.html',
  styleUrl: './ticket-list.component.css'
})
export class TicketListComponent implements OnInit {
  tickets = signal<Ticket[]>([]);
  filteredTickets = signal<Ticket[]>([]);
  loading = signal(true);
  error = signal('');
  filterStatus = signal('ALL');

  constructor(private ticketService: TicketService) {}

  ngOnInit(): void {
    this.loadTickets();
  }

  loadTickets(): void {
    this.loading.set(true);
    this.ticketService.getMyTickets().subscribe({
      next: (response) => {
        this.loading.set(false);
        if (response.success) {
          this.tickets.set(response.data);
          this.applyFilter();
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Failed to load tickets');
      }
    });
  }

  setFilter(status: string): void {
    this.filterStatus.set(status);
    this.applyFilter();
  }

  applyFilter(): void {
    const status = this.filterStatus();
    if (status === 'ALL') {
      this.filteredTickets.set(this.tickets());
    } else {
      this.filteredTickets.set(this.tickets().filter(t => t.status === status));
    }
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
