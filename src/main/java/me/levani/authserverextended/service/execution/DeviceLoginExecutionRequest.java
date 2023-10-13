package me.levani.authserverextended.service.execution;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.levani.authorizationserver.exeption.CustomHttpStatus;
import me.levani.authorizationserver.exeption.ServerException;
import me.levani.authorizationserver.mappers.UserMapper;
import me.levani.authorizationserver.model.core.ExecutionRequest;
import me.levani.authorizationserver.model.core.SecureRequestChain;
import me.levani.authorizationserver.model.domain.RealmUser;
import me.levani.authorizationserver.model.response.PayloadResponse;
import me.levani.authorizationserver.service.UserService;
import me.levani.authorizationserver.utils.ParserUtils;
import me.levani.authserverextended.facade.RealmUserFacade;
import me.levani.authserverextended.model.domain.TrustedDevice;
import me.levani.authserverextended.model.enums.CustomHeaders;
import me.levani.authserverextended.model.redis.DeviceChallenge;
import me.levani.authserverextended.service.ChallengeService;
import me.levani.authserverextended.service.TrustedDeviceService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
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
            return;
        }
       try{
           Optional<TrustedDevice> deviceOptional = trustedDeviceService.findByDeviceId(deviceId, username);
           if (deviceOptional.isEmpty()) {
               throw new ServerException(CustomHttpStatus.BAD_REQUEST,"Device not found!;");
           }
           if (challenge == null) {
               DeviceChallenge deviceChallenge = challengeService.createChallenge(deviceId);
               response.setHeader(CustomHeaders.CHALLENGE.getName(), deviceChallenge.getChallenge());
               throw new ServerException(CustomHttpStatus.UNAUTHORIZED);

           }
           Optional<DeviceChallenge> challengeOptional = challengeService.findById(deviceId);
           if (challengeOptional.isEmpty()) {
               DeviceChallenge deviceChallenge = challengeService.createChallenge(deviceId);
               response.setHeader(CustomHeaders.CHALLENGE.getName(), deviceChallenge.getChallenge());
               throw new ServerException(CustomHttpStatus.UNAUTHORIZED);

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
                   throw new ServerException(CustomHttpStatus.UNAUTHORIZED,"Wrong challenge");
               }

               RealmUser byUsernameAndRealmName = userService.findByUsernameAndRealmName(username, realmName);
               UserMapper.mapOpenIdInfo(byUsernameAndRealmName,payloadResponse);
               UserMapper.mapBasicInfo(byUsernameAndRealmName,payloadResponse);
           }
       }catch ( NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
               InvalidKeySpecException | BadPaddingException | InvalidKeyException ioException){
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
       }

    }
}
