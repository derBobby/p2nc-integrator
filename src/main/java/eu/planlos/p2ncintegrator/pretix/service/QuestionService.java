package eu.planlos.p2ncintegrator.pretix.service;

import eu.planlos.p2ncintegrator.pretix.model.Answer;
import eu.planlos.p2ncintegrator.pretix.model.PretixId;
import eu.planlos.p2ncintegrator.pretix.model.Question;
import eu.planlos.p2ncintegrator.pretix.model.dto.single.AnswerDTO;
import eu.planlos.p2ncintegrator.pretix.model.dto.single.QuestionDTO;
import eu.planlos.p2ncintegrator.pretix.repository.QuestionRepository;
import eu.planlos.p2ncintegrator.pretix.service.api.PretixApiQuestionService;
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

    public Question loadOrFetch(String event, PretixId questionId) {

        // Get from DB
        Optional<Question> questionOptional = questionRepository.findByPretixId(questionId);
        if(questionOptional.isPresent()) {
            log.info("Loaded question from db: {}", questionId);
            return questionOptional.get();
        }

        // or fetch from Pretix
        return fetchFromPretix(event, questionId);
    }

    public void fetchAll(String event) {
        List<QuestionDTO> questionsDTOList = pretixApiQuestionService.queryAllQuestions(event);
        List<Question> questionList = questionsDTOList.stream().map(this::convert).collect(Collectors.toList());
        questionRepository.saveAll(questionList);
    }

    private Question fetchFromPretix(String event, PretixId questionId) {
        QuestionDTO questionDTO = pretixApiQuestionService.queryQuestion(event, questionId);
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

    public Map<Question, Answer> generateQuestionAnswerMap(String event, List<Answer> answerList) {
        Map<Question, Answer> QnAmap = new HashMap<>();
        answerList.forEach(answer -> QnAmap.put(loadOrFetch(event, answer.getPretixId()), answer));
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

