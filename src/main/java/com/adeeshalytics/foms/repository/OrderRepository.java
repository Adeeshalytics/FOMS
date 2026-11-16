package com.adeeshalytics.foms.repository;

import com.adeeshalytics.foms.domain.Order;
import com.adeeshalytics.foms.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByClientIdAndClientOrderId(String clientId, String clientOrderId);
    @Query("select o from Order o join fetch o.instrument where o.instrument.id = :instrumentId and o.status in :statuses")
    List<Order> findActiveByInstrumentId(@Param("instrumentId") UUID instrumentId, @Param("statuses") Collection<OrderStatus> statuses);
    @Query("select o from Order o join fetch o.instrument where o.status in :statuses")
    List<Order> findAllActive(@Param("statuses") Collection<OrderStatus> statuses);
}
