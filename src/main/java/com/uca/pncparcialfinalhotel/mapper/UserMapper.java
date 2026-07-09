package com.uca.pncparcialfinalhotel.mapper;

import com.uca.pncparcialfinalhotel.dto.request.UserRequest;
import com.uca.pncparcialfinalhotel.dto.response.UserResponse;
import com.uca.pncparcialfinalhotel.model.Hotel;
import com.uca.pncparcialfinalhotel.model.User;
import com.uca.pncparcialfinalhotel.model.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request, Hotel hotel) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole() != null ? request.getRole() : Role.CLIENT)
                .hotel(hotel)
                .build();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .hotelId(user.getHotel() != null ? user.getHotel().getId() : null)
                .hotelName(user.getHotel() != null ? user.getHotel().getName() : null)
                .build();
    }

    public void updateEntity(User user, UserRequest request, Hotel hotel) {
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        user.setHotel(hotel);
    }
}