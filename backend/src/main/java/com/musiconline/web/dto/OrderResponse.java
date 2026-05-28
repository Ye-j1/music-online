package com.musiconline.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderResponse {
    public Long    orderId;
    public Long    vinylId;
    public String  title;
    public String  artist;
    public String  vinylType;
    public String  imageUrl;
    public BigDecimal pricePaid;
    public String  sellerUsername;
    public Instant orderedAt;
}
