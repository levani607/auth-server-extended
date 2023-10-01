package me.levani.authserverextended.repository;

import me.levani.authserverextended.model.domain.TrustedDevice;
import me.levani.authserverextended.model.redis.DeviceChallenge;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceChallengeRepository extends CrudRepository<DeviceChallenge,String> {
}
