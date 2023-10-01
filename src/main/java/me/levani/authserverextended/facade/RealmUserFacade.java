package me.levani.authserverextended.facade;

import lombok.RequiredArgsConstructor;
import me.levani.authorizationserver.model.domain.Realm;
import me.levani.authorizationserver.model.domain.RealmUser;
import me.levani.authorizationserver.model.enums.EntityStatus;
import me.levani.authorizationserver.service.RealmService;
import me.levani.authorizationserver.service.UserService;
import me.levani.authserverextended.model.domain.TrustedDevice;
import me.levani.authserverextended.service.TrustedDeviceService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RealmUserFacade {

    private final UserService userService;
    private final RealmService realmService;
    private final PasswordEncoder passwordEncoder;
    private final TrustedDeviceService trustedDeviceService;

    public RealmUser createUser(String username,
                                String password,
                                String firstname,
                                String lastname,
                                String middleName,
                                String realmName) {
        Realm realm = realmService.findByName(realmName);
        if (userService.existByUsername(username, realm.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        RealmUser realmUser = new RealmUser();
        realmUser.setRealm(realm);
        realmUser.setFirstname(firstname);
        realmUser.setLastname(lastname);
        realmUser.setMiddleName(middleName);
        realmUser.setUsername(username);
        realmUser.setEntityStatus(EntityStatus.ACTIVE);
        realmUser.setPassword(passwordEncoder.encode(password));
        return userService.save(realmUser);
    }


    public void registerDevice(String username, String password, String deviceId, String publicKey, String realmName) {
        RealmUser user = userService.findByUsernameAndRealmName(username, realmName);
        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        Optional<TrustedDevice> deviceOptional = trustedDeviceService.findByDeviceId(deviceId,username);
        if(deviceOptional.isPresent()){
            TrustedDevice trustedDevice = deviceOptional.get();
            trustedDevice.setPublicKey(Base64.decodeBase64URLSafe(publicKey));
            trustedDeviceService.save(trustedDevice);
            return;
        }
        TrustedDevice trustedDevice = new TrustedDevice();
        trustedDevice.setDeviceId(deviceId);
        trustedDevice.setEntityStatus(EntityStatus.ACTIVE);
        trustedDevice.setUser(user);
        trustedDevice.setPublicKey(Base64.decodeBase64(publicKey));
        trustedDeviceService.save(trustedDevice);
    }
}
