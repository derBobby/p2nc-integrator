package eu.planlos.p2ncintegrator.pretix.repository;

import eu.planlos.p2ncintegrator.pretix.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByEventAndCode(String event, String code);
}
