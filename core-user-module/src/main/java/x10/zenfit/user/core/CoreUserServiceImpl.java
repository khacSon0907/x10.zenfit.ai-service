package x10.zenfit.user.core;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.zenfit.common.domain.entities.UserEntity;

@AllArgsConstructor
@Service
public class CoreUserServiceImpl implements ICoreUserService{

    @Override
    public UserEntity createUser(CreateUserReq req) {
        return null;
    }
}
