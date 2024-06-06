package com.todo.user.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "notes")
public class Notes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;
    private String noteTitle;
    @Lob
    private String noteMassage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @JoinColumn(name = "user")
    @ManyToOne
    @JsonIgnoreProperties("notes")
    private User user;
}
