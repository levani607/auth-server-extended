package me.levani.authserverextended.service.execution;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.levani.authorizationserver.mappers.UserMapper;
import me.levani.authorizationserver.model.core.ExecutionRequest;
import me.levani.authorizationserver.model.core.SecureRequestChain;
import me.levani.authorizationserver.model.domain.RealmUser;
import me.levani.authorizationserver.model.response.PayloadResponse;
import me.levani.authorizationserver.service.UserService;
import me.levani.authorizationserver.utils.ParserUtils;
import me.levani.authserverextended.facade.RealmUserFacade;
import me.levani.authserverextended.model.domain.TrustedDevice;
import me.levani.authserverextended.model.redis.DeviceChallenge;
import me.levani.authserverextended.service.ChallengeService;
import me.levani.authserverextended.service.TrustedDeviceService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Cipher;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class DeviceLoginExecutionRequest implements ExecutionRequest {

    private final ChallengeService challengeService;
    private final TrustedDeviceService trustedDeviceService;
    private final UserService userService;

    @Override
    public String getName() {
        return "device_login";
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, SecureRequestChain chain, PayloadResponse payloadResponse) {
        String deviceId = request.getParameter("device_id");
        String username = request.getParameter("username");
        String realmName = ParserUtils.getRealmNameFromUri(request.getRequestURI());
        String challenge = request.getParameter("challenge");
        if (deviceId == null) {
            chain.doFilter(request, response, payloadResponse);
        }
       try{
           Optional<TrustedDevice> deviceOptional = trustedDeviceService.findByDeviceId(deviceId, username);
           if (deviceOptional.isEmpty()) {

               response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

           }
           if (challenge == null) {
               DeviceChallenge deviceChallenge = challengeService.createChallenge(deviceId);
               response.setHeader("request_challenge", deviceChallenge.getChallenge());
               response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

           }
           Optional<DeviceChallenge> challengeOptional = challengeService.findById(deviceId);
           if (challengeOptional.isEmpty()) {
               DeviceChallenge deviceChallenge = challengeService.createChallenge(deviceId);
               response.setHeader("request_challenge", deviceChallenge.getChallenge());
               response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

           } else {
               DeviceChallenge deviceChallenge = challengeOptional.get();
               TrustedDevice trustedDevice = deviceOptional.get();
               byte[] publicKeyBytes = trustedDevice.getPublicKey();
               Cipher rsa = Cipher.getInstance("RSA");
               KeyFactory keyFactory = KeyFactory.getInstance("RSA");
               PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
               rsa.init(Cipher.DECRYPT_MODE, publicKey);
               byte[] decryptedBytes = rsa.doFinal(Base64.decodeBase64(challenge));
               String decryptedChallenge = new String(decryptedBytes);
               if (!deviceChallenge.getChallenge().equals(decryptedChallenge)) {
                   response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
               }

               RealmUser byUsernameAndRealmName = userService.findByUsernameAndRealmName(username, realmName);
               UserMapper.mapOpenIdInfo(byUsernameAndRealmName,payloadResponse);
               UserMapper.mapBasicInfo(byUsernameAndRealmName,payloadResponse);
           }
       }catch (IOException ioException){
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
       }catch (Exception e){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
       }

    }
}
