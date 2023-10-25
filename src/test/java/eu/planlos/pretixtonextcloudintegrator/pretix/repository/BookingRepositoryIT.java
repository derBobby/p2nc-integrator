package eu.planlos.pretixtonextcloudintegrator.pretix.repository;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class BookingRepositoryIT {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void injectedComponentsAreNotNull() {
        assertThat(bookingRepository).isNotNull();
    }

    @Test
    public void insertDuplicate_throwsException() {
        Booking firstBooking = new Booking("first event", "XC0DE", "Firstname", "Lastname", "email@example.com", LocalDateTime.now(), new ArrayList<>());
        Booking secondBooking = new Booking("second event", "XC0DE", "Firstname", "Lastname", "email@example.com", LocalDateTime.now(), new ArrayList<>());

        bookingRepository.saveAndFlush(firstBooking);
        assertThrows(DataIntegrityViolationException.class, () -> bookingRepository.saveAndFlush(secondBooking));
    }
}
