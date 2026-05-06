export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: 'STUDENT' | 'ADMIN';
  createdAt?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
}
