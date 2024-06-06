package com.todo.user.serviceImpl;

import com.todo.user.dto.MailDto;
import com.todo.user.dto.NotesDto;
import com.todo.user.entities.Notes;
import com.todo.user.entities.User;
import com.todo.user.exception.Response;
import com.todo.user.feign.SmsMail_Microservices;
import com.todo.user.repository.NotesRepo;
import com.todo.user.repository.UserRepo;
import com.todo.user.service.NotesService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class NotesServiceImpl implements NotesService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private NotesRepo notesRepo;
    @Autowired
    private SmsMail_Microservices smsMailMicroservices;
    @Autowired
    private ModelMapper modelMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotesServiceImpl.class);

    private final ExecutorService service = Executors.newFixedThreadPool(5);

    @Override
    public Response<Object> createNote(NotesDto notesDto) {
        try {
            Notes note = modelMapper.map(notesDto, Notes.class);
            Optional<User> user = userRepo.findById(notesDto.getUserId());
            if (user.isEmpty()) {
                return new Response<>(404, "user not found");
            }
            note.setCreatedAt(LocalDateTime.now());
            note.setUpdatedAt(LocalDateTime.now());
            Notes save = notesRepo.save(note);
            String name = user.get().getUserName();
            String phone = String.valueOf(user.get().getUserPhone());
            MailDto mailDto = new MailDto(user.get().getUserEmail(), note.getNoteTitle(), note.getNoteMassage());
            service.submit(() -> {
                Response<Object> sendSms = smsMailMicroservices.sendSms(phone, "Note created successfully :" + note.getNoteTitle());
                Response<Object> sendEmail = smsMailMicroservices.sendEmail(mailDto, name);
                LOGGER.info("sendSms {}:", sendSms.getStatusCode());
                LOGGER.info("sendEmail {}:", sendEmail.getStatusCode());
            });
            return new Response<>(201, "Note created successfully", note);
        } catch (Exception e) {
            LOGGER.error("Error creating note: {}", e.getMessage());
            return new Response<>(500, "Internal Server Error: ");
        }
    }

    @Override
    public Response<Object> updateNote(Long noteId, NotesDto notesDto) {
        try {
            Optional<Notes> optionalNote = notesRepo.findById(noteId);
            if (optionalNote.isPresent()) {
                Notes note = optionalNote.get();
//                modelMapper.map(notesDto, note);
                Notes notes = mapToNotes(notesDto, note);

                notesRepo.save(notes);
                return new Response<>(200, "Note updated successfully", note);
            } else {
                return new Response<>(404, "Note not found");
            }
        } catch (Exception e) {
            LOGGER.error("Error updating note: {}", e.getMessage());
            return new Response<>(500, "Internal Server Error: ");
        }
    }

    @Override
    public Response<Object> deleteNote(Long noteId) {
        try {
            Optional<Notes> optionalNote = notesRepo.findById(noteId);
            if (optionalNote.isPresent()) {
                notesRepo.deleteById(noteId);
                return new Response<>(200, "Note deleted successfully");
            } else {
                return new Response<>(404, "Note not found");
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting note: {}", e.getMessage());
            return new Response<>(500, "Internal Server Error: ");
        }
    }

    @Override
    public Response<Object> getNoteById(Long noteId) {
        try {
            Optional<Notes> optionalNote = notesRepo.findById(noteId);
            if (optionalNote.isPresent()) {
                return new Response<>(200, "Note found", optionalNote.get());
            } else {
                return new Response<>(404, "Note not found");
            }
        } catch (Exception e) {
            LOGGER.error("Error retrieving note: {}", e.getMessage());
            return new Response<>(500, "Internal Server Error: ");
        }
    }

    @Override
    public Response<Object> getAllNotes(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Notes> notesPage = notesRepo.findAll(pageable);
            if (notesPage.hasContent()) {
                return new Response<>(200, "Notes found",
                        notesPage.stream()
                                .map(note -> modelMapper.map(note, NotesDto.class))
                                .collect(Collectors.toList())
                );
            } else {
                return new Response<>(404, "No notes found");
            }
        } catch (Exception e) {
            LOGGER.error("Error retrieving notes: {}", e.getMessage());
            return new Response<>(500, "Internal Server Error: ");
        }
    }

    private Notes mapToNotes(NotesDto notesDto, Notes note) {

        if (notesDto.getNoteTitle() != null) {
            note.setNoteTitle(notesDto.getNoteTitle());
        }
        if (notesDto.getNoteMassage() != null) {
            note.setNoteMassage(notesDto.getNoteMassage());
        }
        note.setUpdatedAt(LocalDateTime.now());
        return note;
    }

    @PreDestroy
    public void sutDown() {
        service.shutdown();
    }
}
