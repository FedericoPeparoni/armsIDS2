package ca.ids.abms.modules.selfcareportal.userregistration;

import ca.ids.abms.modules.selfcareportal.flightcostcalculation.SCFlightCostCalculationService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SCUserRegistrationService {
    private final Logger log = LoggerFactory.getLogger(SCFlightCostCalculationService.class);
    private UserRepository userRepository;

    public SCUserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Boolean activateUser(String key) {
        log.debug("Request to activate a self-care user with the key : {}", key);
        User user = userRepository.getOneByEmailActivationKey(key);
        if (user != null) {
            user.setRegistrationStatus(true);
            user.setActivationKeyExpiration(null);
            user.setEmailActivationKey(null);
            return true;
        }
        return false;
    }
}
