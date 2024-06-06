package com.todo.user.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class NotesDto {
    private String noteTitle;
    private String noteMassage;
    private Long userId;
}
