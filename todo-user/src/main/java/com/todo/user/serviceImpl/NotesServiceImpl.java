package com.todo.user.serviceImpl;

import com.todo.user.dto.MailDto;
import com.todo.user.dto.NotesDto;
import com.todo.user.entities.FallBackSendEmail;
import com.todo.user.entities.FallBackSendSms;
import com.todo.user.entities.Notes;
import com.todo.user.entities.User;
import com.todo.user.enums.FileType;
import com.todo.user.exception.Response;
import com.todo.user.feign.SmsMail_Microservices;
import com.todo.user.repository.FallBackSendEmailRepo;
import com.todo.user.repository.FallBackSendSmsRepo;
import com.todo.user.repository.NotesRepo;
import com.todo.user.repository.UserRepo;
import com.todo.user.service.NotesService;
import com.todo.user.util.CloudinaryUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class NotesServiceImpl implements NotesService {
    @Autowired
    private CloudinaryUtil cloudinaryUtil;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private NotesRepo notesRepo;
    @Autowired
    private SmsMail_Microservices smsMailMicroservices;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FallBackSendEmailRepo fallBackSendEmailRepo;
    private static final Logger LOGGER = LoggerFactory.getLogger(NotesServiceImpl.class);
    @Autowired
    private FallBackSendSmsRepo fallBackSendSmsRepo;

    @Override
    public Response<Object> createNote(NotesDto notesDto) {
        final ExecutorService service = Executors.newFixedThreadPool(5);
        try {
            Notes note = modelMapper.map(notesDto, Notes.class);
            Optional<User> user = userRepo.findById(notesDto.getUserId());
            if (user.isEmpty()) {
                return new Response<>(404, "user not found");
            }
            if (user.get().getNotes().size() >= 10L && !user.get().getIsSubscribed()) {
                return new Response<>(402, "You are  Service is Full plz if you add next todo then get subscription");
            }
            note.setCreatedAt(LocalDateTime.now());
            note.setUpdatedAt(LocalDateTime.now());
            String base64 = note.getNoteLogo();
            note.setNoteLogo("null");
            Notes save = notesRepo.save(note);
            if (base64 != null) {
                service.submit(() -> {
                    String noteLogoUrl = cloudinaryUtil.uploadFileNew(base64, FileType.IMAGE);
                    save.setNoteLogo(noteLogoUrl);
                    notesRepo.save(save);
                });
            }
            String name = user.get().getUserName();
            String phone = String.valueOf(user.get().getUserPhone());
            MailDto mailDto = new MailDto(user.get().getUserEmail(), note.getNoteTitle(), note.getNoteMassage());
            service.submit(() -> {
                Response<Object> sendSms1 = smsMailMicroservices.sendSms(phone, "Note created successfully :" + note.getNoteTitle());
                LOGGER.info("sendSms {}:", sendSms1.getStatusCode());
                if (sendSms1.getStatusCode() != 200) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        LOGGER.error("Error :{}", e.getMessage());
                    }
                    Response<Object> sendSms2 = smsMailMicroservices.sendSms(phone, "Note created successfully :" + note.getNoteTitle());
                    if (sendSms2.getStatusCode() != 200) {
                        FallBackSendSms backSendSms = new FallBackSendSms();
                        backSendSms.setToSend(phone);
                        backSendSms.setLocalDate(LocalDateTime.now());
                        backSendSms.setMassage(note.getNoteTitle() + " " + "Note Created Successfully ! ,at:" + LocalDateTime.now());
                        backSendSms.setUserName(user.get().getUserName());
                        fallBackSendSmsRepo.save(backSendSms);
                        LOGGER.error("SMS Service is down . Data Save in Data Base ");
                    }
                }

            });
            service.submit(() -> {
                Response<Object> sendEmail1 = smsMailMicroservices.sendEmail(mailDto, name);
                if (sendEmail1.getStatusCode() != 200) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        LOGGER.error("Error :{}", e.getMessage());
                    }
                    Response<Object> sendEmail2 = smsMailMicroservices.sendEmail(mailDto, name);
                    if (sendEmail2.getStatusCode() != 200) {
                        FallBackSendEmail fallBackSendEmail = new FallBackSendEmail();
                        fallBackSendEmail.setUserName(name);
                        fallBackSendEmail.setToSend(mailDto.getTo());
                        fallBackSendEmail.setSubject(mailDto.getSubject());
                        fallBackSendEmail.setBody(mailDto.getBody());
                        fallBackSendEmail.setLocalDate(LocalDateTime.now());
                        FallBackSendEmail saved = fallBackSendEmailRepo.save(fallBackSendEmail);
                        LOGGER.info("sendEmail2 {}:", saved);
                    }
                }
            });
            return new Response<>(201, "Note created successfully", note);
        } catch (Exception e) {
            LOGGER.error("Error creating note: {}", e.getMessage());
            return new Response<>(500, "Internal Server Error: ");
        } finally {
            service.shutdown();
        }
    }

    @Override
    public Response<Object> updateNote(Long noteId, NotesDto notesDto) {
        try {
            Optional<Notes> optionalNote = notesRepo.findById(noteId);
            if (optionalNote.isPresent()) {
                Notes note = optionalNote.get();
//              modelMapper.map(notesDto, note);
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
    public Response<Object> getAllNotes(Long userId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
//            Page<Notes> notesPage = notesRepo.findAll(pageable);
            Page<Notes> notesPage = notesRepo.findByUser_UserIDOrderByCreatedAtDesc(userId, pageable);

            if (notesPage.hasContent()) {
//                return new Response<>(200, "Notes found",
//                        notesPage.stream()
//                                .map(note -> modelMapper.map(note, NotesDto.class))
//                                .collect(Collectors.toList())
//                );
                return new Response<>(200, "SuccessFully Fetch Data ", notesPage);
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

//    @PreDestroy
//    public void sutDown() {
//        service.shutdown();
//    }
}
