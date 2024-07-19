package com.inv.walletCare.logic.entity.passwordReset;

/**
 * Represents a request to reset a password, containing the necessary information
 * such as the OTP, new password, and the email address.
 */
public class ResetPasswordRequest {
    private String otp;
    private String newPassword;
    private String email;

    /**
     * Gets the email address associated with this request.
     *
     * @return the email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address for this request.
     *
     * @param email the email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the OTP (One-Time Password) associated with this request.
     *
     * @return the OTP.
     */
    public String getOtp() {
        return otp;
    }

    /**
     * Sets the OTP (One-Time Password) for this request.
     *
     * @param otp the OTP to set.
     */
    public void setOtp(String otp) {
        this.otp = otp;
    }

    /**
     * Gets the new password to be set for the user.
     *
     * @return the new password.
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Sets the new password for the user.
     *
     * @param newPassword the new password to set.
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
