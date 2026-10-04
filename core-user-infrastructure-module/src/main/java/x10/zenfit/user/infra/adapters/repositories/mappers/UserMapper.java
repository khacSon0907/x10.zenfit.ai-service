package x10.zenfit.user.infra.adapters.repositories.mappers;

import org.mapstruct.Mapper;
import x10.zenfit.common.domain.entities.UserEntity;
import x10.zenfit.user.infra.datasources.mongo.UserDocument;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDocument toDocument(UserEntity entity);

    UserEntity toEntity(UserDocument document);
}