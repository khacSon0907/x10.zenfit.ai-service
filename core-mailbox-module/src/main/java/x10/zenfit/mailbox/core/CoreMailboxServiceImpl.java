package x10.zenfit.mailbox.core;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreMailboxServiceImpl implements ICoreMailboxService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Override
    public void sendOtp(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Mã xác thực của bạn");
            helper.setText(
                    "<p>Mã OTP của bạn là: <b>" + otp + "</b></p>"
                            + "<p>Mã có hiệu lực trong 5 phút. Không chia sẻ mã này cho bất kỳ ai.</p>",
                    true
            );
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}", to, e);
            throw new IllegalStateException("Cannot send OTP email", e);
        }
    }
}