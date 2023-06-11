package eu.planlos.pretixtonextcloudintegrator.pretix.repository;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
    Optional<ProductType> findByPretixId(Long pretixId);
}
