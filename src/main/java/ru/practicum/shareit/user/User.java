package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    private Set<Long> itemIds;
}
