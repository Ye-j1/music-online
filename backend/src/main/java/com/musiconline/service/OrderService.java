package com.musiconline.service;

import com.musiconline.domain.AppUser;
import com.musiconline.domain.Order;
import com.musiconline.domain.Vinyl;
import com.musiconline.repository.AppUserRepository;
import com.musiconline.repository.OrderRepository;
import com.musiconline.repository.VinylRepository;
import com.musiconline.web.dto.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final VinylRepository vinylRepository;
    private final AppUserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        VinylRepository vinylRepository,
                        AppUserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.vinylRepository  = vinylRepository;
        this.userRepository   = userRepository;
    }

    @Transactional
    public OrderResponse placeOrder(Long vinylId, String buyerEmail) {
        AppUser buyer = userRepository.findByEmailIgnoreCase(buyerEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Vinyl vinyl = vinylRepository.findById(vinylId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vinyl not found"));

        if (!vinyl.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This vinyl is no longer available");
        }

        // Prevent self-purchase
        if (vinyl.getSeller().getEmail().equals(buyerEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot purchase your own listing");
        }

        Order order = new Order();
        order.setBuyer(buyer);
        order.setVinyl(vinyl);
        order.setPricePaid(vinyl.getPrice());
        orderRepository.save(order);

        return toResponse(order);
    }

    public List<OrderResponse> myOrders(String buyerEmail) {
        return orderRepository.findByBuyerEmailOrderByOrderedAtDesc(buyerEmail)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse toResponse(Order o) {
        OrderResponse r = new OrderResponse();
        r.orderId        = o.getId();
        r.vinylId        = o.getVinyl().getId();
        r.title          = o.getVinyl().getTitle();
        r.artist         = o.getVinyl().getArtist();
        r.vinylType      = o.getVinyl().getType().name();
        r.imageUrl       = o.getVinyl().getImageUrl();
        r.pricePaid      = o.getPricePaid();
        r.sellerUsername = o.getVinyl().getSeller().getUsername();
        r.orderedAt      = o.getOrderedAt();
        return r;
    }
}
