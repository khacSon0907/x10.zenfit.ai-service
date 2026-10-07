package x10.zenfit.auth.core.usecases;

import x10.zenfit.auth.core.usecases.signInUc.SignInUcReq;
import x10.zenfit.auth.core.usecases.signInUc.SignInUcResp;

public interface ICoreAuthService {

    SignInUcResp signIn(SignInUcReq req);

}
