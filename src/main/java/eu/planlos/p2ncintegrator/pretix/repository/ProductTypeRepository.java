package eu.planlos.p2ncintegrator.pretix.repository;

import eu.planlos.p2ncintegrator.pretix.model.PretixId;
import eu.planlos.p2ncintegrator.pretix.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
    Optional<ProductType> findByPretixId(PretixId pretixId);
}
