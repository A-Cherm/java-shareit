package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private boolean available;
}
