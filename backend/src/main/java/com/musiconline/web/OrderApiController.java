package com.musiconline.web;

import com.musiconline.service.OrderService;
import com.musiconline.web.dto.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** POST /api/orders  body: { "vinylId": 5 } */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@RequestBody Map<String, Long> body,
                                    @AuthenticationPrincipal UserDetails ud) {
        Long vinylId = body.get("vinylId");
        if (vinylId == null) throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.BAD_REQUEST, "vinylId is required");
        return orderService.placeOrder(vinylId, ud.getUsername());
    }

    /** GET /api/orders/mine */
    @GetMapping("/mine")
    public List<OrderResponse> myOrders(@AuthenticationPrincipal UserDetails ud) {
        return orderService.myOrders(ud.getUsername());
    }
}
