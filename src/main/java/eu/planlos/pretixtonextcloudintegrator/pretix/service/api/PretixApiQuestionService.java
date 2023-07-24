package eu.planlos.pretixtonextcloudintegrator.pretix.service.api;

import eu.planlos.pretixtonextcloudintegrator.common.ApiException;
import eu.planlos.pretixtonextcloudintegrator.common.web.PretixContext;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixApiConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixId;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.list.QuestionsDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.QuestionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PretixApiQuestionService extends PretixApiService {

    private static final String FETCH_MESSAGE = "Fetched question from Pretix: {}";

    public PretixApiQuestionService(PretixApiConfig pretixApiConfig, @Qualifier("PretixWebClient") WebClient webClient, PretixContext pretixContext) {
        super(pretixApiConfig, webClient, pretixContext);
    }

    /*
     * Query
     */
    public List<QuestionDTO> queryAllQuestions() {

        QuestionsDTO dto = webClient
                .get()
                .uri(questionListUri())
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

    public QuestionDTO queryQuestion(PretixId questionId) {

        QuestionDTO questionDto = webClient
                .get()
                .uri(specificQuestionUri(questionId.getValue()))
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
    private String specificQuestionUri(Long questionId) {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", pretixEvent(),
                "/questions/", questionId.toString(), "/");
    }

    private String questionListUri() {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", pretixEvent(),
                "/questions/");
    }
}