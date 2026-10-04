package x10.zenfit.user.core.usecases.createUserUc;

import x10.zenfit.common.domain.entities.UserEntity;

public interface ICreateUserUc {
    UserEntity process(CreateUserUcReq req);
}
