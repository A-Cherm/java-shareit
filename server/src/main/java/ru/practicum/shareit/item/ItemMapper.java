package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.RequestItem;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {
    public static ItemDto mapToItemDto(Item item, List<Comment> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                comments.stream().map(ItemMapper::mapToCommentDto).toList(),
                item.getRequest() == null ? null : item.getRequest().getId()
        );
    }

    public static ItemBookingsDto mapToItemBookingsDto(Item item, List<Comment> comments,
                                                       LocalDateTime last, LocalDateTime next) {
        return new ItemBookingsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                comments.stream().map(ItemMapper::mapToCommentDto).toList(),
                last,
                next
        );
    }

    public static Item mapToItem(ItemDto itemDto, User user, ItemRequest request) {
        return new Item(
                itemDto.getId(),
                user,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                request
        );
    }

    public static RequestItemDto mapToRequestItemDto(RequestItem item) {
        return new RequestItemDto(
                item.getId(),
                item.getName(),
                item.getUser().getId()
        );
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getAuthor().getName(),
                comment.getText(),
                comment.getCreated()
        );
    }

    public static Comment mapToComment(CommentDto commentDto, User user, Item item) {
        return new Comment(
                commentDto.getId(),
                item,
                user,
                commentDto.getText(),
                LocalDateTime.now()
        );
    }
}
