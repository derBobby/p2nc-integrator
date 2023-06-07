package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.Question;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.QuestionDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.repository.QuestionRepository;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuestionService {

    private final PretixApiQuestionService pretixApiQuestionService;
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository, PretixApiQuestionService pretixApiQuestionService) {
        this.questionRepository = questionRepository;
        this.pretixApiQuestionService = pretixApiQuestionService;
    }

    public void saveAll(List<QuestionDTO> questionDTOList) {
        List<Question> questionList = questionDTOList.stream().map(this::convert).collect(Collectors.toList());
        questionRepository.saveAll(questionList);
    }

    public List<Question> loadAll() {
        return questionRepository.findAll();
    }

    public Question loadOrFetch(Long questionId) {

        // Get from DB
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if(questionOptional.isPresent()) {
            log.info("Loaded question from db: {}", questionId);
            return questionOptional.get();
        }

        // or fetch from Pretix
        return fetchFromPretix(questionId);
    }

    public void fetchAll() {
        List<QuestionDTO> questionsDTOList = pretixApiQuestionService.queryAllQuestions();
        List<Question> questionList = questionsDTOList.stream().map(this::convert).collect(Collectors.toList());
        questionRepository.saveAll(questionList);
    }

    private Question fetchFromPretix(Long questionId) {
        QuestionDTO questionDTO = pretixApiQuestionService.queryQuestion(questionId);
        Question question = convert(questionDTO);
        return questionRepository.save(question);
    }

    private Question convert(QuestionDTO questionDTO) {

        if(questionDTO.question().size() != 1) {
            throw new UnsupportedOperationException("Name translation for item not implemented");
        }

        return new Question(
                questionDTO.id(),
                questionDTO.question().get("de-informal")
        );
    }
}
