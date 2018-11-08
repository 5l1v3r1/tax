package com.softactive.security;

public interface ISecurityUserService {

    String validatePasswordResetToken(long id, String token);

}
