package ru.practicum.shareit.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Обновление предмета")
public class ItemUpdateDto {
    @Schema(description = "Название предмета", example = "Молоток")
    private String name;
    @Schema(description = "Описание предмета", example = "Просто молоток")
    private String description;
    @Schema(description = "Доступность предмета", example = "true")
    private Boolean available;
}
