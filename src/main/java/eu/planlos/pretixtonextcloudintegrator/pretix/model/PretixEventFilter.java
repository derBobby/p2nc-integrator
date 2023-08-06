package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@NoArgsConstructor
public class PretixEventFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String event;

    @Lob
    @Column
    private final List<PretixQnaFilter> pretixQnaFilterList = new ArrayList<>();
}
