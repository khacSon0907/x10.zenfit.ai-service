package x10.zenfit.user.core.usecases.createUserUc;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import x10.zenfit.common.domain.entities.UserEntity;
import x10.zenfit.common.exceptions.BusinessException;
import x10.zenfit.user.core.errors.UserErrorCode;
import x10.zenfit.user.core.repositories.IUserRepository;

@Service
@AllArgsConstructor
@Slf4j
public class CreateUserImpl implements ICreateUserUc{

    private final IUserRepository userRepository;
    @Override
    public UserEntity process(CreateUserUcReq req) {

       if (userRepository.existsByEmail(req.getEmail())) {
           throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS);
       }
       if (userRepository.existsByUsername(req.getUsername())) {
           throw new BusinessException(UserErrorCode.USERNAME_ALREADY_EXISTS);
       }

       UserEntity user =UserEntity.builder()
               .username(req.getUsername())
               .email(req.getEmail())
               .password(req.getPassword())
               .build();
       return  userRepository.create(user);

    }
}
