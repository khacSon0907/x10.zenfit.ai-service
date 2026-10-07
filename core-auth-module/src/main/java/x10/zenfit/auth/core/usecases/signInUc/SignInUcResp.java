package x10.zenfit.auth.core.usecases.signInUc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInUcResp {
    private String id ;
    private String username ;
}
