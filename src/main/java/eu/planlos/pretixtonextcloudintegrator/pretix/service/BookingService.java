package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.Booking;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Product;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.OrderDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.repository.BookingRepository;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingService {

    private final PretixApiOrderService pretixApiOrderService;
    private final ProductService productService;
    private final BookingRepository bookingRepository;

    public BookingService(PretixApiOrderService pretixApiOrderService, ProductService productService, BookingRepository bookingRepository) {
        this.pretixApiOrderService = pretixApiOrderService;
        this.productService = productService;
        this.bookingRepository = bookingRepository;
    }

    public Booking loadOrFetch(String code) {

        // Get from DB
        Optional<Booking> optionalBooking = bookingRepository.findByCode(code);
        if(optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            log.info("Loaded booking from db: {}", booking);
            return booking;
        }

        // or fetch from Pretix
        return fetchFromPretix(code);
    }

    private Booking fetchFromPretix(String code) {
        OrderDTO orderDTO = pretixApiOrderService.fetchOrderFromPretix(code);
        Booking booking = convert(orderDTO);
        return bookingRepository.save(booking);
    }

    public void fetchAll() {
        List<OrderDTO> orderDTOList = pretixApiOrderService.fetchAllOrders();
        List<Booking> bookingList = orderDTOList.stream().map(this::convert).collect(Collectors.toList());
        bookingRepository.saveAll(bookingList);
    }

    // generateQuestionAnswerMap(orderDTO.getPositions());

    private Booking convert(OrderDTO orderDTO) {
        List<Product> productList = orderDTO.getPositions().stream().map(positionDTO -> productService.loadOrFetchProduct(positionDTO.item(), positionDTO.variation())).toList();
        return new Booking(orderDTO.getCode(), orderDTO.getFirstName(), orderDTO.getLastName(), orderDTO.getEmail(), orderDTO.getExpires(), productList);
    }

//    private Map<Long, Answer> generateQuestionAnswerMap(PositionDTO orderPositionDTO) {
//        Map<Long, Answer> answerHashMap = new HashMap<>();
//        for (AnswerDTO answerDto : orderPositionDTO.answers()) {
//            Question question = questionService.loadOrFetch(answerDto.question());
//            Answer answer = new Answer(answerDto.answer());
//            answerHashMap.put(question.getId(), answer);
//        }
//        return answerHashMap;
//    }
}
