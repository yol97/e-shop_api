package org.greta.eshop_api.domain.services;

import org.greta.eshop_api.exceptions.ResourceNotFoundException;
import org.greta.eshop_api.exposition.dtos.CustomerRequestDTO;
import org.greta.eshop_api.exposition.dtos.CustomerResponseDTO;
import org.greta.eshop_api.mappers.CustomerMapper;
import org.greta.eshop_api.persistence.entities.AddressEntity;
import org.greta.eshop_api.persistence.entities.CustomerEntity;
import org.greta.eshop_api.persistence.repositories.AddressRepository;
import org.greta.eshop_api.persistence.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    /* 👇 On peut faire comme ProductService et injecter le repository
    avec @Autowired sur le champ (sans constructeur):

    @Autowired
    private CustomerRepository customerRepository;

    mais cela est peu recommandé. On préfèrera l'injection par constructeur : */

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    public CustomerService(CustomerRepository customerRepository,
                           AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }

    // 🔹 FIND ALL
    public List<CustomerResponseDTO> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toDto)
                .toList();
    }

    // 🔹 FIND BY ID
    public CustomerResponseDTO findById(Long id) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer introuvable avec l'id " + id));
        return CustomerMapper.toDto(customer);
    }

    // 🔹 CREATE
    public CustomerResponseDTO create(CustomerRequestDTO dto) {

        AddressEntity address = null;
        if (dto.addressId() != null) {
            address = addressRepository.findById(dto.addressId())
                    .orElseThrow(() -> new RuntimeException("Adresse introuvable avec l'id " + dto.addressId()));
        }

        CustomerEntity entity = CustomerMapper.toEntity(dto, address);
        CustomerEntity saved = customerRepository.save(entity);

        return CustomerMapper.toDto(saved);
    }

    // 🔹 UPDATE
    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {

        CustomerEntity existing = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer introuvable avec l'id " + id));

        AddressEntity address = null;
        if (dto.addressId() != null) {
            address = addressRepository.findById(dto.addressId())
                    .orElseThrow(() -> new RuntimeException("Adresse introuvable avec l'id " + dto.addressId()));
        }

        CustomerMapper.updateEntity(existing, dto, address);

        CustomerEntity updated = customerRepository.save(existing);

        return CustomerMapper.toDto(updated);
    }

    // 🔹 DELETE
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer introuvable avec l'id " + id);
        }
        customerRepository.deleteById(id);
    }
}