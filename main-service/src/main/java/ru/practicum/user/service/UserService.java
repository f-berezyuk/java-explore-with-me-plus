package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserRequestDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto registerUser(UserRequestDto userRequestDto);

    void delete(Long userId);

    User getOrThrow(Long id);
}
