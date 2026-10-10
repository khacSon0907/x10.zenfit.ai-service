package x10.zenfit.api.portal.service.controller.authenticationControler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import x10.zenfit.auth.core.usecases.ICoreAuthService;
import x10.zenfit.auth.core.usecases.signInUc.SignInUcReq;
import x10.zenfit.auth.core.usecases.signInUc.SignInUcResp;
import x10.zenfit.common.dto.response.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final ICoreAuthService coreAuthService;


    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<SignInUcResp>> signIn(@Valid @RequestBody SignInUcReq req) {
        SignInUcResp resp = coreAuthService.signIn(req);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }
}