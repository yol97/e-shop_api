package org.greta.eshop_api.exposition.dtos;

import java.time.LocalDateTime;

public record OrderResponseDTO(
   Long id,
   String status,
   Long customerId,
   LocalDateTime createdAt,
   LocalDateTime updatedAt
) {}
