package com.uca.pncparcialfinalhotel.security;

import com.uca.pncparcialfinalhotel.exception.ForbiddenException;
import com.uca.pncparcialfinalhotel.model.User;
import com.uca.pncparcialfinalhotel.model.enums.Role;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean isAdmin() {
        return getCurrentUser().getRole() == Role.ADMIN;
    }

    public boolean isClient() {
        return getCurrentUser().getRole() == Role.CLIENT;
    }

    public boolean isReceptionist() {
        return getCurrentUser().getRole() == Role.RECEPTIONIST;
    }

    public Long requireReceptionistHotelId() {
        User user = getCurrentUser();
        if (user.getHotel() == null) {
            throw new ForbiddenException("Receptionist has no assigned hotel");
        }
        return user.getHotel().getId();
    }
}