package se.teknikhogskolan.springcasemanagement.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.teknikhogskolan.springcasemanagement.model.SecurityUser;
import se.teknikhogskolan.springcasemanagement.repository.SecurityUserRepository;
import se.teknikhogskolan.springcasemanagement.service.exception.NotAllowedException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotAuthorizedException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotFoundException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.generateSalt;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.generateToken;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.hashPassword;
import static se.teknikhogskolan.springcasemanagement.service.SecurityHelper.hashingIterations;

@Service
public class SecurityUserService {

    private  final SecurityUserRepository repository;

    @Autowired
    public SecurityUserService(SecurityUserRepository securityUserRepository) {
        this.repository = securityUserRepository;
    }

    /** @return id */
    public Long create(String username, String password) throws IllegalArgumentException {
        if (null == username) throw new IllegalArgumentException("Username must not be null");
        if (null == password) throw new IllegalArgumentException("Password must not be null");
        if (null != repository.findByUsername(username)) throw new IllegalArgumentException(String.format(
                "Username '%s' already exist", username));

        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        Map<String, String> tokens = new HashMap<>();
        tokens.put(generateToken(254), LocalDateTime.now().plusDays(1L).toString());

        SecurityUser user = repository.save(new SecurityUser(username, tokens, hashedPassword, salt, hashingIterations));

        return user.getId();
    }

    public String createTokenFor(String username, String password) {
        SecurityUser user = getByUsername(username);

        if (passwordMatchesUser(password, user)) {
            String token = generateToken(255);
            user.addToken(token, LocalDateTime.now().plusDays(1));
            repository.save(user);
            return token;
        } else throw new NotAuthorizedException("Wrong password");
    }

    private SecurityUser getByUsername(String username) {

        Optional<SecurityUser> user = Optional.ofNullable(repository.findByUsername(username));
        if (!user.isPresent()) throw new NotFoundException(String.format("No such User '%s'", username));

        return removeExpiredTokens(user.get());
    }

    private boolean passwordMatchesUser(String password, SecurityUser user) {
        if (equalPasswords(password, user.getSalt(), user.getHashedPassword())) {
            return true;
        } else return false;
    }

    private SecurityUser removeExpiredTokens(SecurityUser user) {
        LocalDateTime now = LocalDateTime.now();
        user.getTokensExpiration().forEach((token, expirationDate) -> {
            if (now.isAfter(LocalDateTime.parse(expirationDate))) user.getTokensExpiration().remove(token);
        });
        return repository.save(user);
    }

    private boolean equalPasswords(String password, String salt, String hashedPassword) {
        return hashedPassword.equals(hashPassword(password, salt));
    }

    public Long delete(String username, String password) {
        SecurityUser user = getByUsername(username);
        if (passwordMatchesUser(password, user)) {
            repository.delete(user.getId());
            return user.getId();
        } else throw new NotAuthorizedException("Wrong password");
    }

    private SecurityUser getByToken(String token) {
        SecurityUser user = repository.findByToken(token);
        user = removeExpiredTokens(user);
        if (user.getTokensExpiration().containsKey(token)) {
            return user;
        } else throw new NotAllowedException("Token expired");
    }

    public boolean verify(String username, String token) {
        SecurityUser user = getByToken(token);
        if (username.equals(user.getUsername())) {
            return true;
        } else return false;
    }

    public boolean usernameIsAvailable(String username) {
        return null == repository.findByUsername(username);
    }

    @Deprecated
    public LocalDateTime getExpiration(String token) throws NotImplementedException{
        throw new NotImplementedException();
//        return LocalDateTime.parse(repository.getTokenExpiration(token));
    }
}