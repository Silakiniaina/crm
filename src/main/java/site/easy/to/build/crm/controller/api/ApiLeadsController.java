package site.easy.to.build.crm.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.util.Response;
import site.easy.to.build.crm.util.ResponseUtil;

@RestController
@RequestMapping("/api/leads")
public class ApiLeadsController {

    private final LeadServiceImpl leadService;

    public ApiLeadsController(LeadServiceImpl leadImpl){
        this.leadService = leadImpl;
    }
    
    @SuppressWarnings("unchecked")
    @GetMapping
    public <T> ResponseEntity<Response<T>> getAllLeads(){
        try {
            List<Lead> leads = leadService.findAll();
            return ResponseUtil.sendResponse(HttpStatus.OK, true, "Leads fetched successfully", (T)leads);
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, false, "Error while fetching leads", (T)e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @DeleteMapping("/{id}")
    public <T> ResponseEntity<Response<T>> deleteLead(@PathVariable("id") int id){
        try {
            Lead lead = leadService.findByLeadId(id);
            leadService.delete(lead);
            return ResponseUtil.sendResponse(HttpStatus.OK, true, "Leads deleted successfully", null);
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, false, "Error while deleting lead id : "+id, (T)e.getMessage());
        }
    }
}
