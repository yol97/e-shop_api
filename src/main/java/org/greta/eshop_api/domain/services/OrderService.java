package org.greta.eshop_api.domain.services;

import org.greta.eshop_api.domain.rules.OrderRules;
import org.greta.eshop_api.exceptions.BusinessRuleException;
import org.greta.eshop_api.exceptions.ResourceNotFoundException;
import org.greta.eshop_api.exposition.dtos.OrderItemRequestDTO;
import org.greta.eshop_api.exposition.dtos.OrderRequestDTO;
import org.greta.eshop_api.exposition.dtos.OrderResponseDTO;
import org.greta.eshop_api.mappers.OrderMapper;
import org.greta.eshop_api.persistence.entities.CustomerEntity;
import org.greta.eshop_api.persistence.entities.OrderItemEntity;
import org.greta.eshop_api.persistence.entities.OrdersEntity;
import org.greta.eshop_api.persistence.entities.ProductEntity;
import org.greta.eshop_api.persistence.repositories.CustomerRepository;
import org.greta.eshop_api.persistence.repositories.OrderRepository;
import org.greta.eshop_api.persistence.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    // 🔹 FIND ALL
    public List<OrderResponseDTO> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    // 🔹 FIND BY ID
    public OrderResponseDTO findById(Long id) {
        OrdersEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande introuvable avec l'id " + id
                ));
        return OrderMapper.toDto(order);
    }

    // 🔹 CREATE
    public OrderResponseDTO create(OrderRequestDTO orderDto, List<OrderItemRequestDTO> itemsDto) {

        // 1️⃣ Vérifier le statut
        OrderRules.validateOrderStatus(orderDto.status());

        // 2️⃣ Charger le client puis appliquer la règle métier
        CustomerEntity customer = customerRepository.findById(orderDto.customerId())
                .orElse(null);
        OrderRules.validateCustomer(customer); // lève ResourceNotFound / BusinessRule si besoin

        // 3️⃣ Vérifier qu'il y a au moins un item
        if (itemsDto == null || itemsDto.isEmpty()) {
            throw new BusinessRuleException("Une commande doit contenir au moins un article.");
        }

        // 4️⃣ Créer l'entité OrdersEntity (sans items pour l'instant)
        OrdersEntity order = OrderMapper.toEntity(orderDto, customer);

        List<ProductEntity> productsInOrder = new ArrayList<>();
        double total = 0.0;

        // 5️⃣ Construire les OrderItemEntity
        for (OrderItemRequestDTO itemDto : itemsDto) {

            ProductEntity product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Produit introuvable avec l'id " + itemDto.productId()
                    ));

            // garde trace pour validateProducts()
            productsInOrder.add(product);

            // Vérifie le stock pour cet item
            OrderRules.validateStock(itemDto, product);

            // Crée l'OrderItemEntity
            OrderItemEntity item = new OrderItemEntity();
            item.setQuantity(itemDto.quantity());
            item.setUnit_price(product.getPrice()); // on prend le prix du produit
            item.setProduct(product);
            item.setOrder(order);

            order.getItems().add(item);

            total += item.getQuantity() * item.getUnit_price();
        }

        // 6️⃣ Règles globales
        OrderRules.validateProducts(productsInOrder);
        OrderRules.validateTotal(total);

        // 7️⃣ Sauvegarde
        OrdersEntity saved = orderRepository.save(order);

        return OrderMapper.toDto(saved);
    }

    // 🔹 UPDATE (on remplace les infos + les items)
    public OrderResponseDTO update(Long id, OrderRequestDTO orderDto, List<OrderItemRequestDTO> itemsDto) {

        OrdersEntity existing = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande introuvable avec l'id " + id
                ));

        // 1️⃣ Vérifier le statut
        OrderRules.validateOrderStatus(orderDto.status());

        // 2️⃣ Charger le client puis appliquer la règle métier
        CustomerEntity customer = customerRepository.findById(orderDto.customerId())
                .orElse(null);
        OrderRules.validateCustomer(customer);

        // 3️⃣ Vérifier qu'il y a au moins un item
        if (itemsDto == null || itemsDto.isEmpty()) {
            throw new BusinessRuleException("Une commande doit contenir au moins un article.");
        }

        // 4️⃣ Mettre à jour les infos de base (status + client)
        OrderMapper.updateEntity(existing, orderDto, customer);

        // 5️⃣ Effacer les anciens items (orphanRemoval = true dans OrdersEntity)
        existing.getItems().clear();

        List<ProductEntity> productsInOrder = new ArrayList<>();
        double total = 0.0;

        // 6️⃣ Reconstruire les items
        for (OrderItemRequestDTO itemDto : itemsDto) {

            ProductEntity product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Produit introuvable avec l'id " + itemDto.productId()
                    ));

            productsInOrder.add(product);

            OrderRules.validateStock(itemDto, product);

            OrderItemEntity item = new OrderItemEntity();
            item.setQuantity(itemDto.quantity());
            item.setUnit_price(product.getPrice());
            item.setProduct(product);
            item.setOrder(existing);

            existing.getItems().add(item);

            total += item.getQuantity() * item.getUnit_price();
        }

        // 7️⃣ Règles globales
        OrderRules.validateProducts(productsInOrder);
        OrderRules.validateTotal(total);

        OrdersEntity updated = orderRepository.save(existing);

        return OrderMapper.toDto(updated);
    }

    // 🔹 DELETE
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Commande introuvable avec l'id " + id
            );
        }
        orderRepository.deleteById(id);
    }
}
