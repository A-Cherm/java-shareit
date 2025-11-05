package ru.practicum.shareit.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Сущность предмета")
public class ItemDto {
    @NotBlank
    @Schema(description = "Название предмета", example = "Молоток")
    private String name;
    @NotBlank
    @Schema(description = "Описание предмета", example = "Просто молоток")
    private String description;
    @NotNull
    @Schema(description = "Доступность предмета", example = "true")
    private Boolean available;
    @Schema(description = "Id запроса на данную вещь", example = "1")
    private Long requestId;
}
