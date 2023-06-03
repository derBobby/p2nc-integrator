package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.Answer;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Order;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Question;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.AnswerDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.OrderDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.PositionDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiOrderService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {

    private final PretixApiOrderService pretixApiOrderService;

    public OrderService(PretixApiOrderService pretixApiOrderService) {
        this.pretixApiOrderService = pretixApiOrderService;
    }


    public Order getOrderForCode(String code) {
        OrderDTO orderDTO = pretixApiOrderService.fetchOrderFromPretix(code);

        Order order = new Order(
                orderDTO.getCode(),
                orderDTO.getFirstName(),
                orderDTO.getLastName(),
                orderDTO.getEmail(),
                orderDTO.getExpires(),
                null,
                null);

        generateQuestionAnswerMap(orderDTO.getPositions());

        return null;
    }

    private Map<Long, Answer> generateQuestionAnswerMap(PositionDTO orderPositionDTO) {
        Map<Long, Answer> answerHashMap = new HashMap<>();
        for (AnswerDTO answerDto : orderPositionDTO.answers()) {
            Question question = questionService.loadOrFetch(answerDto.question());
            Answer answer = new Answer(question, answerDto.answer());
            answerHashMap.put(question.getId(), answer);
        }
        return answerHashMap;
    }

}
