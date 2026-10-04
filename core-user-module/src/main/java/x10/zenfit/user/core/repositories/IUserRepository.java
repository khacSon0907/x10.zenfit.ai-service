package x10.zenfit.user.core.repositories;

import x10.zenfit.common.domain.entities.UserEntity;
import x10.zenfit.user.core.usecases.createUserUc.CreateUserUcReq;


public interface IUserRepository {

    UserEntity create(UserEntity userEntity);
}
