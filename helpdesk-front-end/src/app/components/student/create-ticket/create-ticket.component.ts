import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { TicketService } from '../../../services/ticket.service';
import { CategoryService } from '../../../services/category.service';
import { Category } from '../../../models/ticket.model';

@Component({
  selector: 'app-create-ticket',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, NavbarComponent],
  templateUrl: './create-ticket.component.html',
  styleUrl: './create-ticket.component.css'
})
export class CreateTicketComponent implements OnInit {
  title = '';
  description = '';
  priority = 'MEDIUM';
  categoryId: number | null = null;

  categories = signal<Category[]>([]);
  loading = signal(false);
  error = signal('');
  success = signal('');

  priorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

  constructor(
    private ticketService: TicketService,
    private categoryService: CategoryService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (response) => {
        if (response.success) {
          this.categories.set(response.data);
        }
      }
    });
  }

  onSubmit(): void {
    if (!this.title || !this.description) {
      this.error.set('Title and description are required');
      return;
    }

    this.loading.set(true);
    this.error.set('');

    this.ticketService.createTicket({
      title: this.title,
      description: this.description,
      priority: this.priority as any,
      categoryId: this.categoryId || undefined
    }).subscribe({
      next: (response) => {
        this.loading.set(false);
        if (response.success) {
          this.router.navigate(['/student/tickets', response.data.id]);
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err.error?.message || 'Failed to create ticket');
      }
    });
  }
}
