package ca.ids.abms.modules.formulas.navigation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enroute-air-navigation-charges")
public class NavigationBillingFormulaController {

    private final Logger log = LoggerFactory.getLogger(NavigationBillingFormulaController.class);

    private NavigationBillingFormulaService navigationBillingFormulaService;
    private NavigationBillingFormulaMapper navigationBillingFormulaMapper;

    public NavigationBillingFormulaController(NavigationBillingFormulaService navigationBillingFormulaService,
            NavigationBillingFormulaMapper navigationBillingFormulaMapper) {
        this.navigationBillingFormulaService = navigationBillingFormulaService;
        this.navigationBillingFormulaMapper = navigationBillingFormulaMapper;
    }

    @GetMapping
    public ResponseEntity<Page<NavigationBillingFormulaViewModel>> getAllFormulas(
        @SortDefault(sort = {"upperLimit"}, direction = Sort.Direction.ASC) Pageable pageable) {
        log.debug("REST request to get all formulas");
        final Page<NavigationBillingFormula> page = navigationBillingFormulaService.findAllFormula(pageable);
        final Page<NavigationBillingFormulaViewModel> resultPage = new PageImpl<NavigationBillingFormulaViewModel>(
                navigationBillingFormulaMapper.toViewModel(page), pageable, page.getTotalElements()
                );
        return ResponseEntity.ok().body(resultPage);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<NavigationBillingFormulaViewModel> getFormula(@PathVariable Integer id) {
        log.debug("REST request to get the formula with id {}", id);
        final NavigationBillingFormula formula = navigationBillingFormulaService.getFormulaById(id);
        final NavigationBillingFormulaViewModel formulaDto = navigationBillingFormulaMapper.toViewModel(formula);
        return Optional.ofNullable(formulaDto)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('enroute_charges_modify')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<NavigationBillingFormulaViewModel> createFormula(@Valid @RequestBody NavigationBillingFormulaViewModel formulaDto) throws URISyntaxException {
        log.debug("REST request to create a new formula");
        if (formulaDto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        final NavigationBillingFormula formula = navigationBillingFormulaMapper.toModel(formulaDto);
        final NavigationBillingFormula result = navigationBillingFormulaService.createFormula(formula);
        final NavigationBillingFormulaViewModel resultDto = navigationBillingFormulaMapper.toViewModel(result);

        return ResponseEntity.created(new URI("/api/enroute-air-navigation-charges/" + result.getId())).body(resultDto);
    }

    @PreAuthorize("hasAuthority('enroute_charges_modify')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<NavigationBillingFormulaViewModel> updateFormula(@PathVariable Integer id, @RequestBody NavigationBillingFormulaViewModel formulaDto) throws URISyntaxException {
        log.debug("REST request to update a formula with id {}", id);
        final NavigationBillingFormula formula = navigationBillingFormulaMapper.toModel(formulaDto);
        NavigationBillingFormula result = navigationBillingFormulaService.updateFormula(id, formula);
        final NavigationBillingFormulaViewModel resultDto = navigationBillingFormulaMapper.toViewModel(result);
        return ResponseEntity.ok().body(resultDto);
    }

    @PreAuthorize("hasAuthority('enroute_charges_modify')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteFormula(@PathVariable Integer id) {
        log.debug("REST request to remove the formula with id {}", id);
        navigationBillingFormulaService.deleteFormula(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value="/validate",method = RequestMethod.POST)
    public ResponseEntity<List<NavigationBillingFormulaValidationViewModel>> validateFormula(@Valid @RequestBody NavigationBillingFormulaViewModel formulaDto) throws URISyntaxException {
        log.debug("REST request to validate formulas, {}", formulaDto);
        if (formulaDto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        final NavigationBillingFormula formula = navigationBillingFormulaMapper.toModel(formulaDto);
        final List<NavigationBillingFormulaValidationViewModel> result = navigationBillingFormulaService.validateNavigationBillingFormula(formula);
        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(value="/validate/new",method = RequestMethod.POST)
    public ResponseEntity<List<NavigationBillingFormulaValidationViewModel>> validateNewFormula(@Valid @RequestBody NavigationBillingFormulaViewModel formulaDto) throws URISyntaxException {
        log.debug("REST request to validate revenue projection formulas, {}", formulaDto);

        final NavigationBillingFormula formula = navigationBillingFormulaMapper.toModel(formulaDto);
        final List<NavigationBillingFormulaValidationViewModel> result = navigationBillingFormulaService.validateNavigationBillingFormula(formula);

        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(value="/upperlimit", method = RequestMethod.GET)
    public ResponseEntity<NavigationBillingFormula> getFormulaByUpperLimit(@RequestParam Double upperLimit) {
        log.debug("REST request to get formula by upper limit");
        NavigationBillingFormula result = navigationBillingFormulaService.getFormulaByUpperLimit(upperLimit);

        return ResponseEntity.ok().body(result);
    }

}
