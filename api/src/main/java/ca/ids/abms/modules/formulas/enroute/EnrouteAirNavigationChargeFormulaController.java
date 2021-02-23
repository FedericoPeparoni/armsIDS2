package ca.ids.abms.modules.formulas.enroute;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enroute-charge-formulas")
public class EnrouteAirNavigationChargeFormulaController {

    private final Logger log = LoggerFactory.getLogger(EnrouteAirNavigationChargeFormulaController.class);
    
    private final EnrouteAirNavigationChargeFormulaService enrouteAirNavigationChargeFormulaService;
    
    private final EnrouteAirNavigationChargeFormulaMapper enrouteAirNavigationChargeFormulaMapper;

    public EnrouteAirNavigationChargeFormulaController(EnrouteAirNavigationChargeFormulaService aEnrouteAirNavigationChargeFormulaService, EnrouteAirNavigationChargeFormulaMapper aEnrouteAirNavigationChargeFormulaMapper) {
        enrouteAirNavigationChargeFormulaService = aEnrouteAirNavigationChargeFormulaService;
        enrouteAirNavigationChargeFormulaMapper = aEnrouteAirNavigationChargeFormulaMapper;
    }

    @PreAuthorize("hasAuthority('enroute_charges_modify')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<EnrouteAirNavigationChargeFormulaViewModel> createEnrouteAirNavigationChargeFormula(@Valid @RequestBody EnrouteAirNavigationChargeFormulaViewModel enrouteAirNavigationChargeFormula) throws URISyntaxException {
        log.debug("REST request to save EnrouteAirNavigationChargeFormula : {}", enrouteAirNavigationChargeFormula);

        if (enrouteAirNavigationChargeFormula.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        final EnrouteAirNavigationChargeFormula itemToCreate = enrouteAirNavigationChargeFormulaMapper.toModel(enrouteAirNavigationChargeFormula);
        EnrouteAirNavigationChargeFormula result = enrouteAirNavigationChargeFormulaService.save(itemToCreate);
        final EnrouteAirNavigationChargeFormulaViewModel resultDto = enrouteAirNavigationChargeFormulaMapper.toViewModel(result);
        return ResponseEntity.created(new URI("/api/enroute-charge-formulas/" + resultDto.getId()))
                .body(resultDto);
    }

    @PreAuthorize("hasAuthority('enroute_charges_modify')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteEnrouteAirNavigationChargeFormula(@PathVariable Integer id) {
        log.debug("REST request to delete EnrouteAirNavigationChargeFormula : {}", id);

        enrouteAirNavigationChargeFormulaService.delete(id);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<EnrouteAirNavigationChargeFormulaViewModel> getAllEnrouteAirNavigationChargeFormulas(
            @SortDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        log.debug("REST request to get all EnrouteAirNavigationChargeFormulas");
        final Page<EnrouteAirNavigationChargeFormula> page = enrouteAirNavigationChargeFormulaService.findAll(pageable);

        return new PageImpl<>(enrouteAirNavigationChargeFormulaMapper.toViewModel(page), pageable, page.getTotalElements());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<EnrouteAirNavigationChargeFormulaViewModel> getEnrouteAirNavigationChargeFormula(@PathVariable Integer id) {
        log.debug("REST request to get EnrouteAirNavigationChargeFormulaCategory : {}", id);

        EnrouteAirNavigationChargeFormula enrouteAirNavigationChargeFormula = enrouteAirNavigationChargeFormulaService.getOne(id);

        return Optional.ofNullable(enrouteAirNavigationChargeFormula)
                .map(result -> new ResponseEntity<>(enrouteAirNavigationChargeFormulaMapper.toViewModel(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('enroute_charges_modify')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<EnrouteAirNavigationChargeFormulaViewModel> updateEnrouteAirNavigationChargeFormula(@RequestBody EnrouteAirNavigationChargeFormulaViewModel enrouteAirNavigationChargeFormulaDto, @PathVariable Integer id) {
        log.debug("REST request to update EnrouteAirNavigationChargeFormula : {}", enrouteAirNavigationChargeFormulaDto);

        final EnrouteAirNavigationChargeFormula enrouteAirNavigationChargeFormula = enrouteAirNavigationChargeFormulaMapper.toModel(enrouteAirNavigationChargeFormulaDto);
        final EnrouteAirNavigationChargeFormula result = enrouteAirNavigationChargeFormulaService.update(id, enrouteAirNavigationChargeFormula);
        final EnrouteAirNavigationChargeFormulaViewModel resultDto = enrouteAirNavigationChargeFormulaMapper.toViewModel(result);

        return ResponseEntity.ok().body(resultDto);
    }
}
