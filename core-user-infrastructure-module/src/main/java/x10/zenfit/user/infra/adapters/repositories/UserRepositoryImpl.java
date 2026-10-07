package x10.zenfit.user.infra.adapters.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import x10.zenfit.common.domain.entities.UserEntity;
import x10.zenfit.user.core.repositories.IUserRepository;
import x10.zenfit.user.infra.adapters.repositories.mappers.UserMapper;
import x10.zenfit.user.infra.datasources.mongo.UserDocument;
import x10.zenfit.user.infra.datasources.mongo.UserMongoRepository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements IUserRepository {

    private final UserMongoRepository userMongoRepository;
    private final UserMapper userMapper;

    @Override
    public UserEntity create(UserEntity userEntity) {
        UserDocument document = userMapper.toDocument(userEntity);
        UserDocument saved = userMongoRepository.save(document);
        return userMapper.toEntity(saved);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMongoRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMongoRepository.existsByUsername(username);
    }
}