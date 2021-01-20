package app.coronawarn.analytics.common.persistence.domain;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.Size;
import java.util.Date;

public class OtpData {
    @Id
    @Size(min = 36, max = 36)
    private String otp;
    private Date expirationDate;

    public OtpData(String otp, Date expirationDate) {
        this.otp = otp;
        this.expirationDate = expirationDate;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
