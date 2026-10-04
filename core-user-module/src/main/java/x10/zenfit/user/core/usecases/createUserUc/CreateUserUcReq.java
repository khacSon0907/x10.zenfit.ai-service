package x10.zenfit.user.core.usecases.createUserUc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserUcReq {
    private String username;
    private String email;
    private String password;
}
