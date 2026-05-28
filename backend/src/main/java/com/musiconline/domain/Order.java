package com.musiconline.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "mo_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private AppUser buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vinyl_id", nullable = false)
    private Vinyl vinyl;

    /** Snapshot price at purchase time */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePaid;

    @Column(nullable = false)
    private Instant orderedAt = Instant.now();

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AppUser getBuyer() { return buyer; }
    public void setBuyer(AppUser buyer) { this.buyer = buyer; }

    public Vinyl getVinyl() { return vinyl; }
    public void setVinyl(Vinyl vinyl) { this.vinyl = vinyl; }

    public BigDecimal getPricePaid() { return pricePaid; }
    public void setPricePaid(BigDecimal pricePaid) { this.pricePaid = pricePaid; }

    public Instant getOrderedAt() { return orderedAt; }
    public void setOrderedAt(Instant orderedAt) { this.orderedAt = orderedAt; }
}
