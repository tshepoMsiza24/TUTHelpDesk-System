import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { User } from '../../../models/user.model';
import { ApiResponse } from '../../../models/api-response.model';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './admin-users.component.html',
  styleUrl: './admin-users.component.css'
})
export class AdminUsersComponent implements OnInit {
  users = signal<User[]>([]);
  loading = signal(true);
  error = signal('');

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.http.get<ApiResponse<User[]>>('http://localhost:8080/api/admin/users').subscribe({
      next: (response) => {
        this.loading.set(false);
        if (response.success) {
          this.users.set(response.data);
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Failed to load users');
      }
    });
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-ZA', {
      day: '2-digit', month: 'short', year: 'numeric'
    });
  }
}
