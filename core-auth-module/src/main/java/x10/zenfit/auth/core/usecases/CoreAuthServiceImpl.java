package x10.zenfit.auth.core.usecases;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.zenfit.auth.core.usecases.signInUc.ISignInUc;
import x10.zenfit.auth.core.usecases.signInUc.SignInUcReq;
import x10.zenfit.auth.core.usecases.signInUc.SignInUcResp;

@Service
@AllArgsConstructor
public class CoreAuthServiceImpl implements ICoreAuthService {

    private final ISignInUc signInUc;

    @Override
    public SignInUcResp signIn(SignInUcReq req) {
        return signInUc.process(req);
    }
}
