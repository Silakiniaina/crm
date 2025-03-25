package site.easy.to.build.crm.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;
import site.easy.to.build.crm.util.Response;
import site.easy.to.build.crm.util.ResponseUtil;

@RestController
@RequestMapping("/api/tickets")
public class ApiTicketsController {

    private final TicketServiceImpl ticketService;

    public ApiTicketsController(TicketServiceImpl ticketImpl){
        this.ticketService = ticketImpl;
    }
    
    @SuppressWarnings("unchecked")
    @GetMapping
    public <T> ResponseEntity<Response<T>> getAllTickets(){
        try {
            List<Ticket> tickets = ticketService.findAll();
            return ResponseUtil.sendResponse(HttpStatus.OK, true, "Tickets fetched successfully", (T)tickets);
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, false, "Error while fetching tickets", (T)e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @DeleteMapping("/{id}")
    public <T> ResponseEntity<Response<T>> deleteTicket(@PathVariable("id") int id){
        try {
            Ticket ticket = ticketService.findByTicketId(id);
            ticketService.delete(ticket);
            return ResponseUtil.sendResponse(HttpStatus.OK, true, "Tickets deleted successfully", null);
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, false, "Error while deleting ticket id : "+id, (T)e.getMessage());
        }
    }

}
