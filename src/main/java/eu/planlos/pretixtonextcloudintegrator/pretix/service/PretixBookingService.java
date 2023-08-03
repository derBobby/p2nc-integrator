package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.*;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Position;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.OrderDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.repository.BookingRepository;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PretixBookingService {

    private final PretixApiOrderService pretixApiOrderService;
    private final ProductService productService;
    private final QuestionService questionService;

    private final BookingRepository bookingRepository;

    public PretixBookingService(PretixApiOrderService pretixApiOrderService, ProductService productService, QuestionService questionService, BookingRepository bookingRepository) {
        this.pretixApiOrderService = pretixApiOrderService;
        this.productService = productService;
        this.questionService = questionService;
        this.bookingRepository = bookingRepository;
    }

    public Booking loadOrFetch(String event, String code) {

        // Get from DB
        Optional<Booking> optionalBooking = bookingRepository.findByCode(code);
        if(optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            log.info("Loaded booking from db: {}", booking);
            return booking;
        }

        // or fetch from Pretix
        return fetchFromPretix(event, code);
    }

    private Booking fetchFromPretix(String event, String code) {
        OrderDTO orderDTO = pretixApiOrderService.fetchOrderFromPretix(event, code);
        Booking booking = convert(event, orderDTO);
        return bookingRepository.save(booking);
    }

    public void fetchAll(String event) {
        List<OrderDTO> orderDTOList = pretixApiOrderService.fetchAllOrders(event);
        List<Booking> bookingList = orderDTOList.stream().map(orderDTO -> convert(event, orderDTO)).collect(Collectors.toList());
        bookingRepository.saveAll(bookingList);
    }

    private Booking convert(String event, OrderDTO orderDTO) {

        List<Position> positionList = new ArrayList<>();

        orderDTO.getPositions().forEach(positionDTO -> {
            Product product = productService.loadOrFetchProduct(event, new PretixId(positionDTO.item()), new PretixId(positionDTO.variation()));
            Map <Question, Answer> QnAmap = questionService.generateQuestionAnswerMap(event, positionDTO.answers().stream().map(questionService::convert).toList());

            positionList.add(new Position(product, QnAmap));
        });

        return new Booking(event, orderDTO.getCode(), orderDTO.getFirstName(), orderDTO.getLastName(), orderDTO.getEmail(), orderDTO.getExpires(), positionList);
    }
}
