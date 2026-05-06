import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ticket, TicketRequest, TicketUpdateRequest, Comment, CommentRequest } from '../models/ticket.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Ticket endpoints
  createTicket(request: TicketRequest): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(`${this.API_URL}/tickets`, request);
  }

  getAllTickets(): Observable<ApiResponse<Ticket[]>> {
    return this.http.get<ApiResponse<Ticket[]>>(`${this.API_URL}/tickets`);
  }

  getMyTickets(): Observable<ApiResponse<Ticket[]>> {
    return this.http.get<ApiResponse<Ticket[]>>(`${this.API_URL}/tickets/my`);
  }

  getTicketById(id: number): Observable<ApiResponse<Ticket>> {
    return this.http.get<ApiResponse<Ticket>>(`${this.API_URL}/tickets/${id}`);
  }

  updateTicket(id: number, request: TicketUpdateRequest): Observable<ApiResponse<Ticket>> {
    return this.http.put<ApiResponse<Ticket>>(`${this.API_URL}/tickets/${id}`, request);
  }

  deleteTicket(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/tickets/${id}`);
  }

  getTicketsByStatus(status: string): Observable<ApiResponse<Ticket[]>> {
    return this.http.get<ApiResponse<Ticket[]>>(`${this.API_URL}/tickets/status/${status}`);
  }

  // Comment endpoints
  addComment(ticketId: number, request: CommentRequest): Observable<ApiResponse<Comment>> {
    return this.http.post<ApiResponse<Comment>>(`${this.API_URL}/tickets/${ticketId}/comments`, request);
  }

  getComments(ticketId: number): Observable<ApiResponse<Comment[]>> {
    return this.http.get<ApiResponse<Comment[]>>(`${this.API_URL}/tickets/${ticketId}/comments`);
  }

  deleteComment(ticketId: number, commentId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/tickets/${ticketId}/comments/${commentId}`);
  }
}
