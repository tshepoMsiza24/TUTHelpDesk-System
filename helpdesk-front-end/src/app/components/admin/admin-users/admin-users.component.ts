import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { User } from '../../../models/user.model';
import { ApiResponse } from '../../../models/api-response.model';

interface UpdateUserPayload {
  fullName: string;
  username: string;
  email: string;
  password: string;
}

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './admin-users.component.html',
  styleUrl: './admin-users.component.css'
})
export class AdminUsersComponent implements OnInit {
  users = signal<User[]>([]);
  loading = signal(true);
  error = signal('');

  // Toast notification
  toast = signal<{ type: 'success' | 'error'; message: string } | null>(null);
  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  // Edit modal
  editModalOpen = signal(false);
  editLoading = signal(false);
  editError = signal('');
  selectedUser: User | null = null;
  editForm: UpdateUserPayload = { fullName: '', username: '', email: '', password: '' };

  // Delete confirm modal
  deleteModalOpen = signal(false);
  deleteLoading = signal(false);
  userToDelete: User | null = null;

  private readonly API = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.http.get<ApiResponse<User[]>>(`${this.API}/users`).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success) this.users.set(res.data);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Failed to load users');
      }
    });
  }

  // ── EDIT ──────────────────────────────────────────────
  openEdit(user: User): void {
    this.selectedUser = user;
    this.editForm = {
      fullName: user.fullName,
      username: user.username,
      email: user.email,
      password: ''
    };
    this.editError.set('');
    this.editModalOpen.set(true);
  }

  closeEdit(): void {
    this.editModalOpen.set(false);
    this.selectedUser = null;
    this.editError.set('');
  }

  saveEdit(): void {
    if (!this.selectedUser) return;

    if (!this.editForm.fullName.trim() || !this.editForm.username.trim() || !this.editForm.email.trim()) {
      this.editError.set('Full name, username and email are required.');
      return;
    }

    if (this.editForm.password && this.editForm.password.length < 6) {
      this.editError.set('Password must be at least 6 characters.');
      return;
    }

    this.editLoading.set(true);
    this.editError.set('');

    // Only send password if filled in
    const payload: Partial<UpdateUserPayload> = {
      fullName: this.editForm.fullName.trim(),
      username: this.editForm.username.trim(),
      email: this.editForm.email.trim()
    };
    if (this.editForm.password) payload.password = this.editForm.password;

    this.http.put<ApiResponse<User>>(`${this.API}/users/${this.selectedUser.id}`, payload).subscribe({
      next: (res) => {
        this.editLoading.set(false);
        if (res.success) {
          this.closeEdit();
          this.loadUsers();
          this.showToast('success', 'Student details updated successfully.');
        }
      },
      error: (err) => {
        this.editLoading.set(false);
        this.editError.set(err.error?.message || 'Failed to update user.');
      }
    });
  }

  // ── DELETE ────────────────────────────────────────────
  openDelete(user: User): void {
    this.userToDelete = user;
    this.deleteModalOpen.set(true);
  }

  closeDelete(): void {
    this.deleteModalOpen.set(false);
    this.userToDelete = null;
  }

  confirmDelete(): void {
    if (!this.userToDelete) return;
    this.deleteLoading.set(true);

    this.http.delete<ApiResponse<void>>(`${this.API}/users/${this.userToDelete.id}`).subscribe({
      next: (res) => {
        this.deleteLoading.set(false);
        if (res.success) {
          const name = this.userToDelete!.fullName;
          this.closeDelete();
          this.loadUsers();
          this.showToast('success', `${name}'s account has been deleted.`);
        }
      },
      error: (err) => {
        this.deleteLoading.set(false);
        this.closeDelete();
        this.showToast('error', err.error?.message || 'Failed to delete user.');
      }
    });
  }

  // ── TOAST ─────────────────────────────────────────────
  private showToast(type: 'success' | 'error', message: string): void {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toast.set({ type, message });
    this.toastTimer = setTimeout(() => this.toast.set(null), 4000);
  }

  dismissToast(): void {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toast.set(null);
  }

  // ── HELPERS ───────────────────────────────────────────
  get studentCount(): number {
    return this.users().filter(u => u.role === 'STUDENT').length;
  }

  get adminCount(): number {
    return this.users().filter(u => u.role === 'ADMIN').length;
  }

  formatDate(date: string): string {
    if (!date) return '—';
    return new Date(date).toLocaleDateString('en-ZA', {
      day: '2-digit', month: 'short', year: 'numeric'
    });
  }
}
