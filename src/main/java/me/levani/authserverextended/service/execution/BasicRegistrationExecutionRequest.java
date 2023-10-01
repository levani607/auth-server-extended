package me.levani.authserverextended.service.execution;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.levani.authorizationserver.mappers.UserMapper;
import me.levani.authorizationserver.model.core.ExecutionRequest;
import me.levani.authorizationserver.model.core.SecureRequestChain;
import me.levani.authorizationserver.model.domain.Realm;
import me.levani.authorizationserver.model.domain.RealmUser;
import me.levani.authorizationserver.model.enums.EntityStatus;
import me.levani.authorizationserver.model.response.PayloadResponse;
import me.levani.authorizationserver.repository.UserRepository;
import me.levani.authorizationserver.service.RealmService;
import me.levani.authorizationserver.service.UserService;
import me.levani.authorizationserver.utils.ParserUtils;
import me.levani.authserverextended.facade.RealmUserFacade;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BasicRegistrationExecutionRequest implements ExecutionRequest {


    private final RealmUserFacade userFacade;

    @Override
    public String getName() {
        return "basic_registration";
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, SecureRequestChain chain, PayloadResponse payloadResponse) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String middleName = request.getParameter("middleName");
        String realmName = ParserUtils.getRealmNameFromUri(request.getRequestURI());
        RealmUser user = userFacade.createUser(username, password, firstname, lastname, middleName, realmName);

//        UserMapper.mapBasicInfo(user,payloadResponse);
//        UserMapper.mapOpenIdInfo(user,payloadResponse);

    }
}
