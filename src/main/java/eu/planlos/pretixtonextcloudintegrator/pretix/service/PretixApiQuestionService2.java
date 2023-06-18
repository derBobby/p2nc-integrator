package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.QuestionDTO;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface PretixApiQuestionService2 {

    @GetExchange("/api/v1/organizers/kvkraichgau/events/zeltlager23ma/questions/")
    List<QuestionDTO> queryAllQuestions();

//    @GetExchange("/api/v1/organizers/{organizer}/events/{event]/questions/")
//    public List<QuestionDTO> queryAllQuestions(String organizer, String event);



}
