import { Component, signal, OnDestroy, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

export interface StrengthResult {
  score: number;       // 0-4
  label: string;
  color: string;
  width: string;
  checks: { label: string; passed: boolean }[];
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnDestroy {

  fullName    = '';
  username    = '';
  email       = '';
  password    = '';
  confirmPwd  = '';

  loading         = signal(false);
  error           = signal('');
  registered      = signal(false);
  registeredName  = signal('');
  countdown       = signal(5);

  showPwd         = signal(false);
  showConfirmPwd  = signal(false);

  // Real-time field errors
  usernameError   = signal('');
  emailError      = signal('');
  confirmError    = signal('');

  private countdownInterval: ReturnType<typeof setInterval> | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  // ── PASSWORD STRENGTH ────────────────────────────────────────────────────
  get strength(): StrengthResult {
    const p = this.password;
    const checks = [
      { label: 'At least 8 characters',          passed: p.length >= 8 },
      { label: 'Uppercase letter (A-Z)',          passed: /[A-Z]/.test(p) },
      { label: 'Lowercase letter (a-z)',          passed: /[a-z]/.test(p) },
      { label: 'Number (0-9)',                    passed: /\d/.test(p) },
      { label: 'Special character (!@#$...)',     passed: /[^A-Za-z0-9]/.test(p) },
    ];
    const score = checks.filter(c => c.passed).length;

    const map: Record<number, { label: string; color: string }> = {
      0: { label: '',          color: '#e5e7eb' },
      1: { label: 'Very Weak', color: '#e02424' },
      2: { label: 'Weak',      color: '#f97316' },
      3: { label: 'Fair',      color: '#FFB81C' },
      4: { label: 'Strong',    color: '#0e9f6e' },
      5: { label: 'Very Strong', color: '#003DA5' },
    };

    return {
      score,
      label: map[score].label,
      color: map[score].color,
      width: `${(score / 5) * 100}%`,
      checks,
    };
  }

  // ── REAL-TIME VALIDATORS ─────────────────────────────────────────────────
  validateUsername(): void {
    const u = this.username.trim();
    if (!u) { this.usernameError.set(''); return; }
    if (!/^\d+$/.test(u))   { this.usernameError.set('Student number must contain digits only.'); return; }
    if (u.length !== 8)     { this.usernameError.set('Student number must be exactly 8 digits.'); return; }
    this.usernameError.set('');
  }

  validateEmail(): void {
    const e = this.email.trim();
    if (!e) { this.emailError.set(''); return; }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(e)) {
      this.emailError.set('Enter a valid email address.');
    } else {
      this.emailError.set('');
    }
  }

  validateConfirm(): void {
    if (!this.confirmPwd) { this.confirmError.set(''); return; }
    this.confirmError.set(
      this.confirmPwd !== this.password ? 'Passwords do not match.' : ''
    );
  }

  get passwordsMatch(): boolean {
    return this.password.length > 0 && this.password === this.confirmPwd;
  }

  // ── SUBMIT ───────────────────────────────────────────────────────────────
  onSubmit(): void {
    this.error.set('');

    // Sanitise — reject HTML tags
    const fields = [this.fullName, this.username, this.email, this.password];
    if (fields.some(f => /<[^>]*>/.test(f))) {
      this.error.set('Invalid characters detected in input.');
      return;
    }

    if (!this.fullName.trim() || !this.username.trim() || !this.email.trim() || !this.password || !this.confirmPwd) {
      this.error.set('All fields are required.');
      return;
    }

    if (this.usernameError() || this.emailError()) {
      this.error.set('Please fix the errors above before continuing.');
      return;
    }

    if (this.password.length < 6) {
      this.error.set('Password must be at least 6 characters.');
      return;
    }

    if (this.password !== this.confirmPwd) {
      this.error.set('Passwords do not match.');
      return;
    }

    this.loading.set(true);

    this.authService.register({
      username:  this.username.trim(),
      email:     this.email.trim(),
      password:  this.password,
      fullName:  this.fullName.trim()
    }).subscribe({
      next: (response) => {
        this.loading.set(false);
        if (response.success) {
          this.authService.logout();
          this.registeredName.set(this.fullName.trim());
          this.registered.set(true);
          this.startCountdown();
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err.error?.message || 'Registration failed. Please try again.');
      }
    });
  }

  // ── COUNTDOWN ────────────────────────────────────────────────────────────
  private startCountdown(): void {
    this.countdown.set(5);
    this.countdownInterval = setInterval(() => {
      const c = this.countdown();
      if (c <= 1) { this.clearCountdown(); this.router.navigate(['/login']); }
      else        { this.countdown.set(c - 1); }
    }, 1000);
  }

  goToLogin(): void { this.clearCountdown(); this.router.navigate(['/login']); }

  private clearCountdown(): void {
    if (this.countdownInterval) { clearInterval(this.countdownInterval); this.countdownInterval = null; }
  }

  togglePwd():        void { this.showPwd.update(v => !v); }
  toggleConfirmPwd(): void { this.showConfirmPwd.update(v => !v); }

  ngOnDestroy(): void { this.clearCountdown(); }
}
