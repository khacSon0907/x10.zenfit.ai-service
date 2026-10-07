package x10.zenfit.auth.core.usecases.signInUc;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInUcReq {
    private String username;
    private String email;
    private String password;
}
