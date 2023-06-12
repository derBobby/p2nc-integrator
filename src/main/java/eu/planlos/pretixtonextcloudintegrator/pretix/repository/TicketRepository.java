package eu.planlos.pretixtonextcloudintegrator.pretix.repository;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByPretixId(Long pretixId);
}