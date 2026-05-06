import { Pipe, PipeTransform } from '@angular/core';
import { Ticket } from '../models/ticket.model';

@Pipe({
  name: 'ticketFilter',
  standalone: true
})
export class TicketFilterPipe implements PipeTransform {
  transform(tickets: Ticket[], status: string): number {
    if (status === 'ALL') return tickets.length;
    return tickets.filter(t => t.status === status).length;
  }
}
