package x10.zenfit.user.core.usecases.createUserUc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import x10.zenfit.common.domain.entities.UserEntity;
import x10.zenfit.common.exceptions.BusinessException;
import x10.zenfit.mailbox.core.ICoreMailboxService;
import x10.zenfit.user.core.errors.UserErrorCode;
import x10.zenfit.user.core.repositories.IUserRepository;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@AllArgsConstructor
@Slf4j
public class CreateUserImpl implements ICreateUserUc {

    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ICoreMailboxService mailboxService;


    @Override
    public UserEntity process(CreateUserUcReq req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BusinessException(UserErrorCode.USERNAME_ALREADY_EXISTS);
        }

        String encodedPassword = req.getPassword() != null ? passwordEncoder.encode(req.getPassword()) : null;

        UserEntity user = UserEntity.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(encodedPassword)
                .build();
        UserEntity created = userRepository.create(user);

        sendOtp(created.getEmail());
        return created;
    }

    private void sendOtp(String email) {
        String otp = generateOtp();
        // Lưu bản hash, không lưu OTP dạng plain text
        mailboxService.sendOtp(email, otp);
        log.info("OTP sent to {}", email); // không log giá trị OTP
    }

    private String generateOtp() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}