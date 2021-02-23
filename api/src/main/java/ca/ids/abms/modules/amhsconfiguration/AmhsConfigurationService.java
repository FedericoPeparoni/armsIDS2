package ca.ids.abms.modules.amhsconfiguration;

import ca.ids.abms.amhs.AmhsAgentStatus;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.services.AbmsCrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.persistence.EntityNotFoundException;

@Service
@SuppressWarnings("WeakerAccess")
public class AmhsConfigurationService extends AbmsCrudService<AmhsConfiguration, Integer> {

    private final AmhsConfigurationRepository amhsConfigurationRepository;
    private final AmhsConfigurationValidator amhsConfigurationValidator;
    private final AmhsAgentConfigService amhsAgentConfigService;

    AmhsConfigurationService(
            final AmhsConfigurationRepository amhsConfigurationRepository,
            final AmhsConfigurationValidator amhsConfigurationValidator,
            final AmhsAgentConfigService amhsAgentConfigService) {
        super(amhsConfigurationRepository);
        this.amhsConfigurationRepository = amhsConfigurationRepository;
        this.amhsConfigurationValidator = amhsConfigurationValidator;
        this.amhsAgentConfigService = amhsAgentConfigService;
    }

    @Transactional(readOnly = true)
    public List<AmhsConfiguration> findAll() {
        return amhsConfigurationRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public AmhsConfiguration getActive() {
        return amhsConfigurationRepository.findByActive (true);
    }
    
    @Transactional
    public AmhsConfiguration create (final AmhsConfiguration entity) {
        amhsConfigurationValidator.validate (entity);
        deactivateOldRecord (entity);
        amhsConfigurationRepository.flush();
        final AmhsConfiguration res = super.create (entity);
        amhsConfigurationRepository.flush();
        amhsAgentConfigService.validateAgentHostConfig (res);
        amhsAgentConfigService.update();
        return res;
    }
    
    @Transactional
    public AmhsConfiguration update (final Integer id, final AmhsConfiguration entity) {
        final AmhsConfiguration old = amhsConfigurationRepository.getOne (id);
        if (old == null) {
            throw ExceptionFactory.persistenceDataManagement(new EntityNotFoundException(),
                ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        if (entity.getVersion() == null) {
            entity.setVersion(old.getVersion());
        }
        amhsConfigurationValidator.validate (entity);
        amhsConfigurationRepository.flush();
        deactivateOldRecord (entity);
        final AmhsConfiguration res = super.update (id, entity);
        amhsConfigurationRepository.flush();
        amhsAgentConfigService.validateAgentHostConfig (res);
        amhsAgentConfigService.update();
        return res;
    }
    
    @Override
    @Transactional
    public void remove (final Integer id) {
        super.remove(id);
        amhsConfigurationRepository.flush();
        amhsAgentConfigService.update();
    }
    
    /**
     * Start the X.400/AMHS agent
     */
    public AmhsAgentStatus startAgent() {
        return amhsAgentConfigService.startAgent();
    }

    /**
     * Stop the X.400/AMHS agent
     */
    public AmhsAgentStatus stopAgent() {
        return amhsAgentConfigService.stopAgent();
    }
    
    /**
     * Restart the X.400/AMHS agent
     */
    public AmhsAgentStatus restartAgent() {
        return amhsAgentConfigService.restartAgent();
    }
    
    /**
     * Get agent status (installed/started)
     */
    public AmhsAgentStatus agentStatus() {
        return amhsAgentConfigService.agentStatus();
    }
    
    // ----------------- private -----------------
    
    /** Deactivate old existing active record if necessary */
    private void deactivateOldRecord (final AmhsConfiguration newEntity) {
        if (newEntity.getActive() != null && newEntity.getActive()) {
            final AmhsConfiguration oldActive = getActive();
            if (oldActive != null && !newEntity.equals(oldActive)) {
                oldActive.setActive (false);
            }
        }
    }

}
