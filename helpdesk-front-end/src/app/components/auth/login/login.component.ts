import { Component, signal, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

const MAX_ATTEMPTS  = 5;
const LOCKOUT_SECS  = 30;

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnDestroy {

  username = '';
  password = '';

  loading    = signal(false);
  error      = signal('');
  showPwd    = signal(false);
  capsLock   = signal(false);

  // Rate-limiting
  failedAttempts = signal(0);
  lockedOut      = signal(false);
  lockCountdown  = signal(LOCKOUT_SECS);
  private lockTimer: ReturnType<typeof setInterval> | null = null;

  // Remember username
  rememberMe = false;

  constructor(private authService: AuthService, private router: Router) {
    const saved = localStorage.getItem('hd_remember_user');
    if (saved) {
      this.username  = saved;
      this.rememberMe = true;
    }
  }

  // ── CAPS LOCK DETECTION ──────────────────────────────────────────────────
  @HostListener('document:keydown', ['$event'])
  onKeyDown(e: KeyboardEvent): void {
    this.capsLock.set(e.getModifierState?.('CapsLock') ?? false);
  }

  @HostListener('document:keyup', ['$event'])
  onKeyUp(e: KeyboardEvent): void {
    this.capsLock.set(e.getModifierState?.('CapsLock') ?? false);
  }

  // ── SUBMIT ───────────────────────────────────────────────────────────────
  onSubmit(): void {
    if (this.lockedOut()) return;

    const u = this.username.trim();
    const p = this.password;

    if (!u || !p) {
      this.error.set('Please enter your username and password.');
      return;
    }

    // Basic injection guard — reject obvious script tags
    if (/<[^>]*>/.test(u) || /<[^>]*>/.test(p)) {
      this.error.set('Invalid characters detected in input.');
      return;
    }

    this.loading.set(true);
    this.error.set('');

    this.authService.login({ username: u, password: p }).subscribe({
      next: (response) => {
        this.loading.set(false);
        if (response.success) {
          this.failedAttempts.set(0);

          if (this.rememberMe) {
            localStorage.setItem('hd_remember_user', u);
          } else {
            localStorage.removeItem('hd_remember_user');
          }

          if (response.data.role === 'ADMIN') {
            this.router.navigate(['/admin/dashboard']);
          } else {
            this.router.navigate(['/student/dashboard']);
          }
        }
      },
      error: (err) => {
        this.loading.set(false);
        const attempts = this.failedAttempts() + 1;
        this.failedAttempts.set(attempts);

        const remaining = MAX_ATTEMPTS - attempts;

        if (attempts >= MAX_ATTEMPTS) {
          this.startLockout();
        } else {
          this.error.set(
            `${err.error?.message || 'Invalid username or password.'} ` +
            `${remaining} attempt${remaining === 1 ? '' : 's'} remaining.`
          );
        }
      }
    });
  }

  // ── LOCKOUT ──────────────────────────────────────────────────────────────
  private startLockout(): void {
    this.lockedOut.set(true);
    this.lockCountdown.set(LOCKOUT_SECS);
    this.error.set('');

    this.lockTimer = setInterval(() => {
      const t = this.lockCountdown() - 1;
      if (t <= 0) {
        this.clearLockout();
      } else {
        this.lockCountdown.set(t);
      }
    }, 1000);
  }

  private clearLockout(): void {
    if (this.lockTimer) { clearInterval(this.lockTimer); this.lockTimer = null; }
    this.lockedOut.set(false);
    this.failedAttempts.set(0);
    this.lockCountdown.set(LOCKOUT_SECS);
    this.error.set('');
  }

  // ── HELPERS ──────────────────────────────────────────────────────────────
  togglePwd(): void { this.showPwd.update(v => !v); }

  get attemptsLeft(): number { return MAX_ATTEMPTS - this.failedAttempts(); }

  get progressWidth(): string {
    return `${(this.lockCountdown() / LOCKOUT_SECS) * 100}%`;
  }

  ngOnDestroy(): void { this.clearLockout(); }
}
