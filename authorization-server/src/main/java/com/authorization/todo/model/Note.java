package com.authorization.todo.model;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Note {
    private Long noteId;
    private String noteTitle;
    private String noteMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}