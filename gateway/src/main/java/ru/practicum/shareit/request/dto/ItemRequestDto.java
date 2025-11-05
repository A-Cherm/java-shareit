package ru.practicum.shareit.request.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос предмета")
public class ItemRequestDto {
    @NotBlank
    @Schema(description = "Описание запрашиваемого предмета", example = "Хочу золотой молоток")
    String description;
}
