package com.book.service;

import com.book.domain.dto.LoginDto;
import org.springframework.stereotype.Service;


@Service
public interface LoginService{
    public String login(LoginDto dto);
}
