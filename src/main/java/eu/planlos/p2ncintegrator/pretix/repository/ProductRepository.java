package eu.planlos.p2ncintegrator.pretix.repository;

import eu.planlos.p2ncintegrator.pretix.model.PretixId;
import eu.planlos.p2ncintegrator.pretix.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByPretixIdAndPretixVariationId(PretixId pretixId, PretixId pretixVariationId);

    Optional<Product> findByPretixIdAndPretixVariationIdIsNull(PretixId pretixId);
}