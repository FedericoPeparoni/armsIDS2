package ca.ids.abms.modules.users;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends ABMSRepository<User, Integer> {

    User getOneByLogin(String login);

    User getOneByEmail(String email);

    List<User> findBySmsNumberAndIdNot(String smsNumber, Integer id);

    List<User> findByEmailAndIdNot(String email, Integer id);

    List<User> findByNameAndIdNot(String name, Integer id);

    List<User> findByLoginAndIdNot(String name, Integer id);

    List<User> findAllBySmsNumber(String smsNumber);

    List<User> findAllByEmail(String email);

    List<User> findAllByName(String name);

    List<User> findAllByLogin(String name);

    @Query(value = "SELECT users.email FROM User users " +
        "JOIN users.userRoles user_roles " +
        "JOIN user_roles.role.rolePermissions role_permissions " +
        "JOIN role_permissions.permission permissions " +
        "WHERE permissions.name = 'self_care_admin'")
    List<String> getSelfCarePortalAdminAddress();

    User getOneByEmailActivationKey(String key);

    @Query(value = "SELECT users.id FROM User users " +
        "WHERE now() > users.activationKeyExpiration and users.registrationStatus is false")
    List<Integer> getExpiredActivationKey();

    @Query(value = "SELECT users FROM User users " +
        "WHERE now() > users.temporaryPasswordExpiration")
    List<User> getExpiredTempPasswords();

    @Query(value = "SELECT COUNT(users) FROM User users WHERE isSelfcareUser is true")
    long countAllSelfcareUsers();
}
