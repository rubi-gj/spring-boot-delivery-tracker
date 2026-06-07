package deliverytracker.controller;

import deliverytracker.entity.Hub;
import deliverytracker.service.HubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for Hub operations.
 * Covers R8 - RESTful Web Services
 */
@RestController
@RequestMapping("/api/hubs")
@CrossOrigin(origins = "*")
public class HubController {

    private final HubService hubService;

    public HubController(HubService hubService) {
        this.hubService = hubService;
    }

    // POST /api/hubs → Shto hub te ri
    @PostMapping
    public ResponseEntity<Hub> createHub(
            @RequestBody Hub hub) {
        return ResponseEntity.ok(
                hubService.createHub(hub));
    }

    // GET /api/hubs → Te gjitha hub-et
    @GetMapping
    public ResponseEntity<List<Hub>> getAllHubs() {
        return ResponseEntity.ok(
                hubService.findAll());
    }

    // GET /api/hubs/city/{city}
    @GetMapping("/city/{city}")
    public ResponseEntity<Hub> getByCity(
            @PathVariable String city) {
        return ResponseEntity.ok(
                hubService.findByCity(city));
    }
}
