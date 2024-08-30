package com.restproject.backend.services.Auth;

import com.restproject.backend.entities.Auth.RefreshToken;
import com.restproject.backend.repositories.RefreshTokenCrud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenCrud refreshTokenCrud;

    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenCrud.save(refreshToken);
    }

    public boolean checkExistRefreshTokenByJwtId(String id) {
        return refreshTokenCrud.existsById(id);
    }

    public void removeRefreshTokenByJwtId(String id) {
        refreshTokenCrud.deleteById(id);
    }
}
