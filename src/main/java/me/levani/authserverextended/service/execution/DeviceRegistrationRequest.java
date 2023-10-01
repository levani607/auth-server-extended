package me.levani.authserverextended.service.execution;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.levani.authorizationserver.model.core.ExecutionRequest;
import me.levani.authorizationserver.model.core.SecureRequestChain;
import me.levani.authorizationserver.model.response.PayloadResponse;
import me.levani.authorizationserver.utils.ParserUtils;
import me.levani.authserverextended.facade.RealmUserFacade;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeviceRegistrationRequest implements ExecutionRequest {

    private final RealmUserFacade realmUserFacade;

    @Override
    public String getName() {
        return "device_registration";
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, SecureRequestChain chain, PayloadResponse payloadResponse) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String deviceId = request.getParameter("device_id");
        String publicKey = request.getParameter("public_key");
        String realmName = ParserUtils.getRealmNameFromUri(request.getRequestURI());
        realmUserFacade.registerDevice(username,password,deviceId,publicKey,realmName);
    }
}
