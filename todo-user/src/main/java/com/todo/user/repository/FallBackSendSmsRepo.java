package com.todo.user.repository;

import com.todo.user.entities.FallBackSendSms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FallBackSendSmsRepo extends JpaRepository<FallBackSendSms, Long> {
}
