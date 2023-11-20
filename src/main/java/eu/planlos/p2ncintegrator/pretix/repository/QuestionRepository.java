package eu.planlos.p2ncintegrator.pretix.repository;

import eu.planlos.p2ncintegrator.pretix.model.PretixId;
import eu.planlos.p2ncintegrator.pretix.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByPretixId(PretixId questionId);
}