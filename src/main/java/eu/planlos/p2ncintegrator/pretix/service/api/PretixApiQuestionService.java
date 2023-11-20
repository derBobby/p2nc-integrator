package eu.planlos.p2ncintegrator.pretix.service.api;

import eu.planlos.p2ncintegrator.common.ApiException;
import eu.planlos.p2ncintegrator.pretix.config.PretixApiConfig;
import eu.planlos.p2ncintegrator.pretix.model.PretixId;
import eu.planlos.p2ncintegrator.pretix.model.dto.list.QuestionsDTO;
import eu.planlos.p2ncintegrator.pretix.model.dto.single.QuestionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class PretixApiQuestionService extends PretixApiService {

    private static final String FETCH_MESSAGE = "Fetched question from Pretix: {}";

    public PretixApiQuestionService(PretixApiConfig pretixApiConfig, @Qualifier("PretixWebClient") WebClient webClient) {
        super(pretixApiConfig, webClient);
    }

    /*
     * Query
     */
    public List<QuestionDTO> queryAllQuestions(String event) {

        if(isAPIDisabled()) {
            return Collections.emptyList();
        }

        QuestionsDTO dto = webClient
                .get()
                .uri(questionListUri(event))
                .retrieve()
                .bodyToMono(QuestionsDTO.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(3)))
                .doOnError(error -> log.error("Message fetching all questions from Pretix API: {}", error.getMessage()))
                .block();

        if (dto != null) {
            List<QuestionDTO> questionDTOList = new ArrayList<>(dto.results());
            questionDTOList.forEach(questionDTO -> log.info(FETCH_MESSAGE, questionDTO));
            return questionDTOList;
        }

        throw new ApiException(ApiException.IS_NULL);
    }

    public QuestionDTO queryQuestion(String event, PretixId questionId) {

        if(isAPIDisabled()) {
            return null;
        }

        QuestionDTO questionDto = webClient
                .get()
                .uri(specificQuestionUri(event, questionId.getValue()))
                .retrieve()
                .bodyToMono(QuestionDTO.class)
                .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                .doOnError(error -> log.error("Message fetching question={} from Pretix API: {}", questionId, error.getMessage()))
                .block();
        if (questionDto != null) {
            log.info(FETCH_MESSAGE, questionDto);
            return questionDto;
        }

        throw new ApiException(ApiException.IS_NULL);
    }

    /*
     * Uri generators
     */
    private String specificQuestionUri(String event, Long questionId) {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", event,
                "/questions/", questionId.toString(), "/");
    }

    private String questionListUri(String event) {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", event,
                "/questions/");
    }
}