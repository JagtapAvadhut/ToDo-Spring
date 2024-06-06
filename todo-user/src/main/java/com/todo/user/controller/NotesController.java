package com.todo.user.controller;

import com.todo.user.dto.NotesDto;
import com.todo.user.exception.Response;
import com.todo.user.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/notes")
public class NotesController {

    @Autowired
    private NotesService notesService;

    @PostMapping
    public Response<Object> createNote(@RequestBody NotesDto notesDto) {
        return notesService.createNote(notesDto);
    }

    @PutMapping("/{noteId}")
    public Response<Object> updateNote(@PathVariable Long noteId, @RequestBody NotesDto notesDto) {
        return notesService.updateNote(noteId, notesDto);
    }

    @DeleteMapping("/{noteId}")
    public Response<Object> deleteNote(@PathVariable Long noteId) {
        return notesService.deleteNote(noteId);
    }

    @GetMapping("/{noteId}")
    public Response<Object> getNoteById(@PathVariable Long noteId) {
        return notesService.getNoteById(noteId);
    }

    @GetMapping
    public Response<Object> getAllNotes(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return notesService.getAllNotes(page,size);
    }
}
