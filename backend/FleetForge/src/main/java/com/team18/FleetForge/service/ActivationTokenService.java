package com.team18.FleetForge.service;

import com.team18.FleetForge.model.ValidationToken;
import com.team18.FleetForge.model.users.Driver;

public interface ActivationTokenService {
    ValidationToken createTokenPasswordSetDriver(Driver driver);
    void saveActivationToken(ValidationToken validationToken);
}
