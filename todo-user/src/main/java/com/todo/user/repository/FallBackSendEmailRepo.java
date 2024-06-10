package com.todo.user.repository;

import com.todo.user.entities.FallBackSendEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FallBackSendEmailRepo extends JpaRepository<FallBackSendEmail, Long> {
}
