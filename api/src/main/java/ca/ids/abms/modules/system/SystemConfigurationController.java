package ca.ids.abms.modules.system;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.ids.abms.modules.reports2.common.RoundingUtils;

@RestController
@RequestMapping("/api/system-configurations")
public class SystemConfigurationController {

    private static final Logger LOG = LoggerFactory.getLogger(SystemConfigurationController.class);

    private final RoundingUtils roundingUtils;

    private final SystemConfigurationMapper systemConfigurationMapper;

    private final SystemConfigurationService systemConfigurationService;

    private final SystemValidationService systemValidationService;

    public SystemConfigurationController(
        final RoundingUtils roundingUtils,
        final SystemConfigurationMapper systemConfigurationMapper,
        final SystemConfigurationService systemConfigurationService,
        final SystemValidationService systemValidationService) {
        this.roundingUtils = roundingUtils;
        this.systemConfigurationMapper = systemConfigurationMapper;
        this.systemConfigurationService = systemConfigurationService;
        this.systemValidationService = systemValidationService;
    }

    @GetMapping(value = "/noauth/getLanguages")
    public List<SystemConfiguration> getNoauthLanguagesSystemConfigurations() {
        LOG.debug("REST request to get languages from system configurations required prior to authentication");

        return this.systemConfigurationService.getNoauthLanguagesSystemConfigurations();
    }

    @GetMapping(value = "/noauth/getUnitsOfMeasure")
    public List<SystemConfiguration> getNoauthUnitsOfMeasureSystemConfigurations() {
        LOG.debug("REST request to get units of measure from system configurations required prior to authentication");

        return this.systemConfigurationService.getNoauthUnitsOfMeasureSystemConfigurations();
    }

    @GetMapping(value = "/noauth/getPasswordSettings")
    public List<SystemConfiguration> getPasswordSettings() {
        LOG.debug("REST request to get password settings");

        return this.systemConfigurationService.getPasswordSettings();
    }

    @GetMapping(value = "/noauth/getSelfCareSettings")
    public List<SystemConfiguration> getSelfCareSettings(@RequestParam(name = "param", required = true) String param) {
        LOG.debug("REST request to get captcha settings");

        return this.systemConfigurationService.getSelfCareSettings(param);
    }

    @GetMapping(value = "/noauth/getAirNavigationChargesCurrency")
    public SystemConfiguration getNoauthAirNavigationChargesCurrency() {
        LOG.debug("REST request to get air navigation charges currency from system configurations required prior to authentication");

        return this.systemConfigurationService.getNoauthAirNavigationChargesCurrency();
    }

    @GetMapping(value = "/noauth/getANSPCurrency")
    public SystemConfiguration getNoauthAnspCurrency() {
        LOG.debug("REST request to get ANSP currency from system configurations required prior to authentication");

        return this.systemConfigurationService.getNoauthAnspCurrency();
    }

    @GetMapping
    public Page<SystemConfigurationViewModel> getSystemConfigurations(@PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        LOG.debug("REST request to get all system configurations");
        final Page<SystemConfiguration> page = systemConfigurationService.findSystemConfigurations(pageable);
        return new PageImpl<>(systemConfigurationMapper.toViewModel(page), pageable, page.getTotalElements());
    }

    @GetMapping(value = "/plugins/{pluginId}")
    public Page<SystemConfigurationViewModel> getPluginConfigurations(
        @PathVariable Integer pluginId, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        LOG.debug("REST request to get plugin id {} configurations", pluginId);
        final Page<SystemConfiguration> page = systemConfigurationService.findPluginConfigurations(pluginId, pageable);
        return new PageImpl<>(systemConfigurationMapper.toViewModel(page), pageable, page.getTotalElements());
    }

    @GetMapping(value = "/storage")
    public Page<SystemConfigurationViewModel> getClientStorageConfigurations(
        @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        LOG.debug("REST request to get client storage configurations");
        final Page<SystemConfiguration> page = systemConfigurationService.findClientStorageConfigurations(pageable);
        return new PageImpl<>(systemConfigurationMapper.toViewModel(page), pageable, page.getTotalElements());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SystemConfigurationViewModel> getSystemConfiguration(@PathVariable Integer id) {
        LOG.debug("REST request to get system configuration : {}", id);

        SystemConfigurationViewModel systemConfiguration = systemConfigurationMapper
                .toViewModel(systemConfigurationService.getOne(id));

        return Optional.ofNullable(systemConfiguration).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('sys_config_modify') or hasAuthority('manage_plugin_modify')")
    @PutMapping
    public ResponseEntity<Collection<SystemConfigurationViewModel>> updateSystemConfiguration(
            @RequestBody Collection<SystemConfigurationViewModel> systemConfigurations) {
        LOG.debug("REST request to update system configurations : {}", systemConfigurations);

        Collection<SystemConfigurationViewModel> result = systemConfigurationMapper
                .toViewModel(systemConfigurationService.update(systemConfigurations));

        // update rounding utils config value
        // required as AVIATION_INVOICE_ROUNDING is cached for performance
        roundingUtils.updateRoundingConfig();

        return ResponseEntity.ok().body(result);
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<Boolean> validateSystemConfiguration(
        @RequestBody SystemConfigurationViewModel systemConfigurationViewModel) {
        LOG.debug("REST request to validate system configuration item : {}", systemConfigurationViewModel);
        return ResponseEntity.ok().body(systemValidationService
            .validateSystemConfiguration(systemConfigurationMapper.toModel(systemConfigurationViewModel)));
    }

    @PostMapping(value = "/validate/fm")
    public ResponseEntity<Boolean> validateSystemConfigurationInvoiceByFm(
        @RequestBody List<SystemConfigurationViewModel> viewModelList) {
        LOG.debug("REST request to validate system configuration item : {}", viewModelList);
        return ResponseEntity.ok().body(systemValidationService.validateSystemConfiguration(viewModelList));
    }
}
