package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.LoginRequest;
import com.uca.pncparcialfinalhotel.dto.request.RefreshTokenRequest;
import com.uca.pncparcialfinalhotel.dto.request.RegisterRequest;
import com.uca.pncparcialfinalhotel.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);
}