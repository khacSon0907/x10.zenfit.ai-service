package x10.zenfit.user.core;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.zenfit.common.domain.entities.UserEntity;
import x10.zenfit.user.core.usecases.createUserUc.CreateUserUcReq;
import x10.zenfit.user.core.usecases.createUserUc.ICreateUserUc;

@AllArgsConstructor
@Service
public class CoreUserServiceImpl implements ICoreUserService{

    private final ICreateUserUc createUserUc;
    @Override
    public UserEntity createUser(CreateUserUcReq req) {
        return createUserUc.process(req);
    }
}
