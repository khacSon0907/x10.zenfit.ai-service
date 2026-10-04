package x10.zenfit.common.domain.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import x10.zenfit.common.domain.enums.Role;
import x10.zenfit.common.domain.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserEntity {
   private String id ;
   private String username;
   private String email;
   private String password;
   private String displayName;
   private String phone ;
   private String location;
    private String avatar;
    private UserStatus status = UserStatus.PENDING;
    private Set<Role> roles = new HashSet<>();
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}