package x10.zenfit.user.core.usecases.createUserUc;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import x10.zenfit.common.domain.entities.UserEntity;
import x10.zenfit.user.core.repositories.IUserRepository;

@Service
@AllArgsConstructor
@Slf4j
public class CreateUserImpl implements ICreateUserUc{

    private final IUserRepository userRepository;
    @Override
    public UserEntity process(CreateUserUcReq req) {

       UserEntity user =UserEntity.builder()
               .username(req.getUsername())
               .email(req.getEmail())
               .password(req.getPassword())
               .build();
       return  userRepository.create(user);

    }
}
