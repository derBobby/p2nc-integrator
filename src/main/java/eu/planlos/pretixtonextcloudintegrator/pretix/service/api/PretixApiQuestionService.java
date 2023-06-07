package eu.planlos.pretixtonextcloudintegrator.pretix.service.api;

import eu.planlos.pretixtonextcloudintegrator.common.ApiException;
import eu.planlos.pretixtonextcloudintegrator.pretix.config.PretixApiConfig;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.list.QuestionsDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.QuestionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
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
    public List<QuestionDTO> queryAllQuestions() {

        QuestionsDTO dto;
        try {
            dto = webClient
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
                return dto.results();
            }

            throw new ApiException(ApiException.IS_NULL);
        } catch (WebClientResponseException e) {
            throw new ApiException(e);
        }
    }

    public QuestionDTO queryQuestion(Long questionId) {

        try {
            QuestionDTO questionDto = webClient
                    .get()
                    .uri(specificQuestionUri(questionId))
                    .retrieve()
                    .bodyToMono(QuestionDTO.class)
                    .retryWhen(Retry.fixedDelay(0, Duration.ofSeconds(1)))
                    .doOnError(error -> log.error("Message fetching question={} from Pretix API: {}", error.getMessage()))
                    .block();
            if(questionDto!=null) {
                log.info(FETCH_MESSAGE, questionDto);
                return questionDto;
            }

            throw new ApiException(ApiException.IS_NULL);
        } catch (WebClientResponseException e) {
            throw new ApiException(e);
        }
    }


    /*
     * Uri generators
     */
    private String specificQuestionUri(Long questionId) {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", pretixApiConfig.event(),
                "/questions/", questionId.toString(), "/");
    }

    private String questionListUri() {
        return String.join(
                "",
                "api/v1/organizers/", pretixApiConfig.organizer(),
                "/events/", pretixApiConfig.event(),
                "/questions/");
    }
}