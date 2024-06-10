package com.todo.user.repository;

import com.todo.user.entities.Notes;
import com.todo.user.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

@Repository
public interface NotesRepo extends JpaRepository<Notes, Long> {
    Page<Notes> findByUser_UserIDOrderByCreatedAtDesc(Long userID, Pageable pageable);

    boolean existsByCreatedAtAndUser(LocalDateTime createdAt, User user);

    @Query("SELECT COUNT(n) > 0 FROM Notes n WHERE DAY(n.createdAt) = :day AND n.user = :user")
    boolean existsNoteForUserOnDay(@Param("day") int day, @Param("user") User user);
}
