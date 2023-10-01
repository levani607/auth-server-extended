package me.levani.authserverextended.model.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "device_challenge",timeToLive = 3000)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceChallenge implements Serializable {
    @Id
    private String deviceId;
    private String challenge;
}
