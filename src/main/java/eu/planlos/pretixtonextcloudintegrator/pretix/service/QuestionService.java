package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.Answer;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixId;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Question;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.AnswerDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.QuestionDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.repository.QuestionRepository;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Question loadOrFetch(PretixId questionId) {

        // Get from DB
        Optional<Question> questionOptional = questionRepository.findByPretixId(questionId);
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

    private Question fetchFromPretix(PretixId questionId) {
        QuestionDTO questionDTO = pretixApiQuestionService.queryQuestion(questionId);
        Question question = convert(questionDTO);
        return questionRepository.save(question);
    }

    private Question convert(QuestionDTO questionDTO) {

        if(questionDTO.question().size() != 1) {
            throw new UnsupportedOperationException("Name translation for item not implemented");
        }

        return new Question(
                new PretixId(questionDTO.id()),
                questionDTO.getName()
        );
    }

    public Answer convert(AnswerDTO answerDTO) {
        return new Answer(new PretixId(answerDTO.question()), answerDTO.answer());
    }

    public Map<Question, Answer> generateQuestionAnswerMap(List<Answer> answerList) {
        Map<Question, Answer> QnAmap = new HashMap<>();
        answerList.forEach(answer -> QnAmap.put(loadOrFetch(answer.getPretixId()), answer));
        return QnAmap;
    }
}


//answerList.addAll(positionDTO.answers().stream().map(answerDTO -> new Answer(answerDTO.answer())).toList());

//    private Map<Long, Answer> generateQuestionAnswerMap(PositionDTO orderPositionDTO) {
//        Map<Long, Answer> answerHashMap = new HashMap<>();
//        for (AnswerDTO answerDto : orderPositionDTO.answers()) {
//            Question question = questionService.loadOrFetch(answerDto.question());
//            Answer answer = new Answer(answerDto.answer());
//            answerHashMap.put(question.getId(), answer);
//        }
//        return answerHashMap;
//    }

