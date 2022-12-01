package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.comment.model.Comment;

public class CommentMapper {

    public static CommentDtoResponse toCommentDtoResponse(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDtoResponse.builder()
                .created(comment.getCreated())
                .author(comment.getAuthor().getName())
                .text(comment.getText())
                .id(comment.getId())
                .build();
    }

}
