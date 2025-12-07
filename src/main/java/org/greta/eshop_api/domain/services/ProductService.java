package org.greta.eshop_api.domain.services;

import org.greta.eshop_api.domain.rules.ProductRules;
import org.greta.eshop_api.exceptions.ResourceNotFoundException;
import org.greta.eshop_api.exposition.dtos.ProductRequestDTO;
import org.greta.eshop_api.exposition.dtos.ProductResponseDTO;
import org.greta.eshop_api.mappers.ProductMapper;
import org.greta.eshop_api.persistence.entities.ProductEntity;
import org.greta.eshop_api.persistence.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    // 👇 Hop on injecte le repository
    @Autowired
    private ProductRepository productRepository;


    // 👇 On a décalé la logique du Controller ici
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toDto)
                .toList();
    }

    // 👇 On a décalé la logique du Controller ici
    public ProductResponseDTO findById(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit avec l’ID " + id + " n’existe pas."));
        return ProductMapper.toDto(product);
    }

    // 👇 On applique les règles métiers de Product + mapping
    public ProductResponseDTO create(ProductRequestDTO dto) {
        ProductEntity entity = ProductMapper.toEntity(dto);
        ProductRules.validateBeforeCreation(entity);
        ProductEntity saved = productRepository.save(entity);
        return ProductMapper.toDto(saved);
    }

    // 👇 On délègue à l'entité d'encapsuler sa logique de mise à jour + on applique les règles métier de Product + mapping
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        ProductEntity existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit " + id + " introuvable."));
        existing.updateFrom(dto);
        ProductRules.validateBeforeUpdate(existing);
        ProductEntity saved = productRepository.save(existing);
        return ProductMapper.toDto(saved);
    }

    // 👇 On a décalé la logique du Controller ici
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Impossible de supprimer : produit " + id + " introuvable.");
        }
        productRepository.deleteById(id);
    }
}