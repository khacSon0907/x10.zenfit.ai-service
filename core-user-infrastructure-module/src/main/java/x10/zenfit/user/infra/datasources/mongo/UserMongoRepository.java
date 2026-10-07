package x10.zenfit.user.infra.datasources.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<UserDocument, String> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}