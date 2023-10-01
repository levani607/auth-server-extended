package me.levani.authserverextended.service;

import lombok.RequiredArgsConstructor;
import me.levani.authserverextended.model.redis.DeviceChallenge;
import me.levani.authserverextended.repository.DeviceChallengeRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChallengeService {

    private final DeviceChallengeRepository challengeRepository;

    public DeviceChallenge createChallenge(String deviceId){
        return challengeRepository.save(new DeviceChallenge(deviceId,RandomStringUtils.randomAlphanumeric(64)));
    }

    public Optional<DeviceChallenge> findById(String deviceId) {
        return challengeRepository.findById(deviceId);
    }
}
