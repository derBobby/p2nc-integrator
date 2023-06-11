package eu.planlos.pretixtonextcloudintegrator.pretix.repository;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.Addon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddonRepository extends JpaRepository<Addon, Long> {
    Optional<Addon> findByPretixId(Long pretixId);
}