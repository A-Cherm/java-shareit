package ru.practicum.shareit.item.model;

public interface RequestItem {
    Long getId();

    String getName();

    UserId getUser();

    RequestId getRequest();

    interface UserId {
        Long getId();
    }

    interface RequestId {
        Long getId();
    }
}
