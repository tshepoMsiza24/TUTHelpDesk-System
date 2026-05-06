import { User } from './user.model';

export type TicketStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type TicketPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export interface Category {
  id: number;
  name: string;
  description?: string;
}

export interface Ticket {
  id: number;
  title: string;
  description: string;
  status: TicketStatus;
  priority: TicketPriority;
  category?: Category;
  student: User;
  assignedTo?: User;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string;
  commentCount: number;
}

export interface TicketRequest {
  title: string;
  description: string;
  priority: TicketPriority;
  categoryId?: number;
}

export interface TicketUpdateRequest {
  title?: string;
  description?: string;
  status?: TicketStatus;
  priority?: TicketPriority;
  categoryId?: number;
  assignedToId?: number;
}

export interface Comment {
  id: number;
  ticketId: number;
  user: User;
  comment: string;
  createdAt: string;
}

export interface CommentRequest {
  comment: string;
}
