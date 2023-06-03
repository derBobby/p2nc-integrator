package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import java.time.LocalDateTime;
import java.util.List;

public record Order(
        String code,
        String firstname,
        String lastname,
        String email,
        LocalDateTime expires,
        List<Booking> bookingList,
        List<Addon> addonList
) {}
