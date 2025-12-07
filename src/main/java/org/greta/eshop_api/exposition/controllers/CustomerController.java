package org.greta.eshop_api.exposition.controllers;
import org.greta.eshop_api.domain.services.CustomerService;
import org.greta.eshop_api.exposition.dtos.CustomerRequestDTO;
import org.greta.eshop_api.exposition.dtos.CustomerResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    /* 👇 On fait une injection par constructeur plutôt qu'avec @Autowired (déprécié)
    @Autowired
    private CustomerService customerService; */

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // 👇👇 Get
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {
        List<CustomerResponseDTO> response = customerService.findAll();
        return ResponseEntity.ok(response);
    }

    // Find by id
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO dto = customerService.findById(id);
        return ResponseEntity.ok(dto);
    }

    // Create
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @RequestBody CustomerRequestDTO request
    ) {
        CustomerResponseDTO dto = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDTO request
    ) {
        CustomerResponseDTO dto = customerService.update(id, request);
        return ResponseEntity.ok(dto);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}
