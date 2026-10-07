package x10.zenfit.auth.core.usecases.signInUc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import x10.zenfit.common.domain.entities.UserEntity;
import x10.zenfit.user.core.ICoreUserService;
import x10.zenfit.user.core.usecases.createUserUc.CreateUserUcReq;

@Service
@AllArgsConstructor
@Slf4j
public class ISignInUcImpl implements ISignInUc {

    private final ICoreUserService coreUserService;

    @Override
    public SignInUcResp process(SignInUcReq req) {
        UserEntity user = coreUserService.createUser(
                CreateUserUcReq.builder()
                        .username(req.getUsername())
                        .email(req.getEmail())
                        .password(req.getPassword())
                        .build());

        return SignInUcResp.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
