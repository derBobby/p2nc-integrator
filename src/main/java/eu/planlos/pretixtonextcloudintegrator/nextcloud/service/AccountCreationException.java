 package eu.planlos.pretixtonextcloudintegrator.nextcloud.service;

 public class AccountCreationException extends RuntimeException {

     public static final String SHORT_USERID = "No free userid can be generated";
     public static final String EMAIL_TAKEN = "Email address is already in use";

     public AccountCreationException(String message) {
        super(message);
    }
}
