package x10.zenfit.user.core;

import x10.zenfit.common.domain.entities.UserEntity;
import x10.zenfit.user.core.usecases.createUserUc.CreateUserUcReq;

public interface ICoreUserService {
    UserEntity createUser(CreateUserUcReq req);
}
