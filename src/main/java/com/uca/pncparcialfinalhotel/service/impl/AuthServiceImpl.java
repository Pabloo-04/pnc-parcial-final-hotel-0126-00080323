package com.uca.pncparcialfinalhotel.service.impl;

import com.uca.pncparcialfinalhotel.dto.request.LoginRequest;
import com.uca.pncparcialfinalhotel.dto.request.RefreshTokenRequest;
import com.uca.pncparcialfinalhotel.dto.request.RegisterRequest;
import com.uca.pncparcialfinalhotel.dto.response.AuthResponse;
import com.uca.pncparcialfinalhotel.exception.InvalidTokenException;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.model.Hotel;
import com.uca.pncparcialfinalhotel.model.User;
import com.uca.pncparcialfinalhotel.model.enums.Role;
import com.uca.pncparcialfinalhotel.repository.HotelRepository;
import com.uca.pncparcialfinalhotel.repository.UserRepository;
import com.uca.pncparcialfinalhotel.security.JwtService;
import com.uca.pncparcialfinalhotel.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }
        Role role = request.getRole() != null ? request.getRole() : Role.CLIENT;
        Hotel hotel = resolveHotel(role, request.getHotelId());
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .hotel(hotel)
                .build();
        userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new InvalidTokenException("The provided token is not a refresh token");
            }
            String email = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new InvalidTokenException("Refresh token is invalid");
            }
            return AuthResponse.builder()
                    .accessToken(jwtService.generateAccessToken(user))
                    .refreshToken(jwtService.generateRefreshToken(user))
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessExpirationMs() / 1000)
                    .build();
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Refresh token has expired, please log in again");
        } catch (InvalidTokenException | ResourceNotFoundException e) {
            throw e;
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid refresh token");
        }
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessExpirationMs() / 1000)
                .build();
    }

    private Hotel resolveHotel(Role role, Long hotelId) {
        if (role != Role.RECEPTIONIST) {
            return null;
        }
        if (hotelId == null) {
            throw new IllegalArgumentException("hotelId is required for RECEPTIONIST role");
        }
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
    }
}