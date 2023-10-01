package me.levani.authserverextended.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.levani.authorizationserver.model.enums.EntityStatus;
import me.levani.authserverextended.model.domain.TrustedDevice;
import me.levani.authserverextended.repository.TrustedDeviceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class TrustedDeviceService {

    private final TrustedDeviceRepository trustedDeviceRepository;

    public TrustedDevice save(TrustedDevice device){
        return trustedDeviceRepository.save(device);
    }

    public Optional<TrustedDevice> findByDeviceId(String deviceId,String userName){


        return trustedDeviceRepository.findByIdAndUserName(deviceId,userName, EntityStatus.ACTIVE);
    }
}
