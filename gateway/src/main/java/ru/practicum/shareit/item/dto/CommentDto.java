package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Комментарий к предмету")
public class CommentDto {
    @Schema(description = "Id автора", example = "1")
    private String authorName;
    @NotBlank
    @Schema(description = "Текст комментария", example = "Лучший молоток на свете")
    private String text;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Дата создания", example = "2001-01-01T00:00:00", type = "string")
    private LocalDateTime created;
}
