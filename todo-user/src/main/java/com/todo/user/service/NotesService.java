package com.todo.user.service;

import com.todo.user.dto.NotesDto;
import com.todo.user.exception.Response;
import org.springframework.data.domain.Pageable;

public interface NotesService {
    Response<Object> createNote(NotesDto notesDto);

    Response<Object> updateNote(Long noteId, NotesDto notesDto);

    Response<Object> deleteNote(Long noteId);

    Response<Object> getNoteById(Long noteId);

    Response<Object> getAllNotes(Long userId, int page, int size);

}
