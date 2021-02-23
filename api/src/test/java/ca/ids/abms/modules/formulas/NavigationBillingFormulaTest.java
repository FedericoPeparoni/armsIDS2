package ca.ids.abms.modules.formulas;

import ca.ids.abms.modules.flightmovements.enumerate.CostFormulaVar;
import ca.ids.abms.modules.formulas.navigation.NavigationBillingFormula;
import ca.ids.abms.modules.formulas.navigation.NavigationBillingFormulaRepository;
import ca.ids.abms.modules.formulas.navigation.NavigationBillingFormulaService;
import ca.ids.abms.modules.formulas.navigation.NavigationBillingFormulaValidationViewModel;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class NavigationBillingFormulaTest {
    private NavigationBillingFormulaRepository navigationBillingFormulaRepository;
    private NavigationBillingFormulaService navigationBillingFormulaService;
    private FormulaEvaluator formulaEvaluator;

    @Before
    public void setup() {
        navigationBillingFormulaRepository = mock(NavigationBillingFormulaRepository.class);
        formulaEvaluator= new FormulaEvaluatorImpl (new JavascriptEngineFactory());
        navigationBillingFormulaService = new NavigationBillingFormulaService(navigationBillingFormulaRepository, formulaEvaluator);
    }

    @Test
    public void getAllNavigationBillingFormulas() throws Exception {
        List<NavigationBillingFormula> navigationBillingFormulas = Collections.singletonList(new NavigationBillingFormula());

        when(navigationBillingFormulaRepository.findAll(any(Pageable.class)))
        .thenReturn(new PageImpl<>(navigationBillingFormulas));

        Page<NavigationBillingFormula> results = navigationBillingFormulaService.findAllFormula(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(navigationBillingFormulas.size());
    }

    @Test
    public void getNavigationBillingFormulaById() throws Exception {
        NavigationBillingFormula navigationBillingFormula = new NavigationBillingFormula();
        navigationBillingFormula.setId(1);

        when(navigationBillingFormulaRepository.getOne(any()))
        .thenReturn(navigationBillingFormula);

        NavigationBillingFormula result = navigationBillingFormulaService.getFormulaById(1);
        assertThat(result).isEqualTo(navigationBillingFormula);
    }

    @Test
    public void createNavigationBillingFormula() throws Exception {
        NavigationBillingFormula navigationBillingFormula = new NavigationBillingFormula();
        navigationBillingFormula.setInternationalArrivalFormula("int_arr_fn()");
        navigationBillingFormula.setUpperLimit(0.5);
        when(navigationBillingFormulaRepository.save(any(NavigationBillingFormula.class)))
        .thenReturn(navigationBillingFormula);

        NavigationBillingFormula result = navigationBillingFormulaService.createFormula(navigationBillingFormula);
        assertThat(result.getInternationalArrivalFormula()).isEqualTo(navigationBillingFormula.getInternationalArrivalFormula());
        assertThat(result.getUpperLimit() == navigationBillingFormula.getUpperLimit());
    }

    @Test
    public void updateNavigationBillingFormula() throws Exception {
        NavigationBillingFormula existingNavigationBillingFormula = new NavigationBillingFormula();
        existingNavigationBillingFormula.setInternationalArrivalFormula("int_arr_fn()");

        NavigationBillingFormula navigationBillingFormula = new NavigationBillingFormula();
        navigationBillingFormula.setInternationalArrivalFormula("new_int_arr_fn()");

        when(navigationBillingFormulaRepository.getOne(any()))
        .thenReturn(existingNavigationBillingFormula);

        when(navigationBillingFormulaRepository.save(any(NavigationBillingFormula.class)))
        .thenReturn(existingNavigationBillingFormula);

        NavigationBillingFormula result = navigationBillingFormulaService.updateFormula(1, navigationBillingFormula);

        assertThat(result.getInternationalArrivalFormula()).isEqualTo("new_int_arr_fn()");
    }

    @Test
    public void deleteNavigationBillingFormula() throws Exception {
        navigationBillingFormulaService.deleteFormula(1);
        verify(navigationBillingFormulaRepository).delete(any(Integer.class));
    }


    @Test
    // this test is valid when all formula are validate
    public void evaluateNavigationBillingFormulaWithVar()throws Exception{

        NavigationBillingFormula navigationBillingFormula=null;
        navigationBillingFormula=new NavigationBillingFormula();
        navigationBillingFormula.setUpperLimit(234.0);
        navigationBillingFormula.setDomesticFormula("sqrt ("+ CostFormulaVar.MTOW.varInlineName()+"/ 50) * ("+CostFormulaVar.SCHEDCROSSDIST.varInlineName()+"/ 100) * 33.28");
        navigationBillingFormula.setRegionalDepartureFormula("("+CostFormulaVar.SCHEDCROSSDIST.varInlineName()+"/ 100) * 12");
        navigationBillingFormula.setRegionalArrivalFormula("(max (0.0,"+CostFormulaVar.SCHEDCROSSDIST.varInlineName()+" - 37) / 100) * 12");
        navigationBillingFormula.setRegionalOverflightFormula("("+CostFormulaVar.SCHEDCROSSDIST.varInlineName()+" / 100) * sqrt ("+ CostFormulaVar.AVGMASSFACTOR.varInlineName()+"/ 10) * 12");

        navigationBillingFormula.setInternationalDepartureFormula("("+CostFormulaVar.SCHEDCROSSDIST.varInlineName()+"/ 100) * sqrt ("+ CostFormulaVar.MTOW.varInlineName()+" / 10) * 12");
        navigationBillingFormula.setInternationalArrivalFormula("(max (0.0, "+ CostFormulaVar.ENTRIESNUMBER.varInlineName()+" - 37) / 100) * sqrt ("+ CostFormulaVar.MTOW.varInlineName()+" * 10) * 12");
        navigationBillingFormula.setInternationalOverflightFormula("("+CostFormulaVar.SCHEDCROSSDIST.varInlineName()+"/ 100) * sqrt ("+ CostFormulaVar.FIRENTRYFEE.varInlineName()+" / 10) * 12");

        List<NavigationBillingFormulaValidationViewModel> navigationBillingFormulaValidationViewModels= navigationBillingFormulaService.validateNavigationBillingFormula(navigationBillingFormula);

        Boolean assertValue=Boolean.FALSE;
        for(NavigationBillingFormulaValidationViewModel navigationBillingFormulaValidationViewModel :navigationBillingFormulaValidationViewModels){
            assertValue=navigationBillingFormulaValidationViewModel.getFormulaValid();
            assertThat(assertValue);
        }
        assertThat(assertValue);
    }


    @Test
    // this test is valid when all formula are validate
    public void evaluateNavigationBillingFormula()throws Exception{

        NavigationBillingFormula navigationBillingFormula=null;
        // this an example of navigationBillingFormula for validation test
        navigationBillingFormula=new NavigationBillingFormula();
        navigationBillingFormula.setUpperLimit(234.0);
        navigationBillingFormula.setDomesticFormula("((5+(30-6*4))*10)/11");
        navigationBillingFormula.setRegionalDepartureFormula("((5+(30-6*4))*10)/11");
        navigationBillingFormula.setRegionalArrivalFormula("((5+(30-6*4))*10)/11");
        navigationBillingFormula.setRegionalOverflightFormula("((5+(30-6*4))*10)/11");

        navigationBillingFormula.setInternationalDepartureFormula("((5+(30-6*4))*10)/11");
        navigationBillingFormula.setInternationalArrivalFormula("((5+(30-6*4))*10)/11");
        navigationBillingFormula.setInternationalOverflightFormula("((5+(30-6*4))*10)/11");

        List<NavigationBillingFormulaValidationViewModel> navigationBillingFormulaValidationViewModels= navigationBillingFormulaService.validateNavigationBillingFormula(navigationBillingFormula);

        Boolean assertValue=Boolean.FALSE;
        for(NavigationBillingFormulaValidationViewModel navigationBillingFormulaValidationViewModel :navigationBillingFormulaValidationViewModels){
            assertValue=navigationBillingFormulaValidationViewModel.getFormulaValid();
        }
        assertThat(assertValue==Boolean.TRUE);
    }

    @Test
    // this test is valid when all formula are validate
    public void evaluateWrongNavigationBillingFormula()throws Exception{

        NavigationBillingFormula navigationBillingFormula=null;
        // this an example of navigationBillingFormula for validation test
        navigationBillingFormula=new NavigationBillingFormula();
        navigationBillingFormula.setUpperLimit(234.0);
        navigationBillingFormula.setDomesticFormula("{[5+(30-6*4)]*10}/11");
        navigationBillingFormula.setRegionalDepartureFormula("[5+(30-6*4)]*10]/11");
        navigationBillingFormula.setRegionalArrivalFormula("[[5+-(30-6*4)]*10]/11");
        navigationBillingFormula.setRegionalOverflightFormula("[[5+(30-/6*4)]*10]/11");

        navigationBillingFormula.setInternationalDepartureFormula("[[5+(30-{6*4})]*10]/11");
        navigationBillingFormula.setInternationalArrivalFormula("[[5+(30-6*4)]*1/0+]/11");
        navigationBillingFormula.setInternationalOverflightFormula("[[*5+(30-6*4)]*10]/11");

        List<NavigationBillingFormulaValidationViewModel> navigationBillingFormulaValidationViewModels= navigationBillingFormulaService.validateNavigationBillingFormula(navigationBillingFormula);

        Boolean assertValue=Boolean.TRUE;
        for(NavigationBillingFormulaValidationViewModel navigationBillingFormulaValidationViewModel :navigationBillingFormulaValidationViewModels){
            assertValue=navigationBillingFormulaValidationViewModel.getFormulaValid();
        }
        assertThat(assertValue==Boolean.FALSE);
    }

    @Test
    // this test is valid when all formula are not validate
    public void evaluateWrongNavigationBillingFormulaWithVar()throws Exception{

        NavigationBillingFormula navigationBillingFormula=null;
        // this an example of navigationBillingFormula for validation test
        navigationBillingFormula=new NavigationBillingFormula();
        navigationBillingFormula.setUpperLimit(234.0);
        // formula is not valid missing bracket(
        navigationBillingFormula.setDomesticFormula("sqrt "+ CostFormulaVar.MTOW.varInlineName()+"/ 50) * ("+CostFormulaVar.SCHEDCROSSDIST.varInlineName()+"/ 100) * 33.28");
        // this formula is valid
        navigationBillingFormula.setRegionalDepartureFormula("("+CostFormulaVar.SCHEDCROSSDIST.varInlineName()+"//// 0) * 12");
        // formula is not valid call function undefined
        navigationBillingFormula.setRegionalArrivalFormula("(mtx (0.0,"+CostFormulaVar.SCHEDCROSSDIST.varInlineName()+" - 37) / 100) * sqrt (PassengerFees * 10) * 12");
        // formula is not valid because there is a wrong operator '/*'
        navigationBillingFormula.setRegionalOverflightFormula("("+CostFormulaVar.SCHEDCROSSDIST.varInlineName()+" /* 100) * sqrt ("+ CostFormulaVar.AVGMASSFACTOR.varInlineName()+"/ 10) * 12");
        // formula null
        navigationBillingFormula.setInternationalDepartureFormula(null);
        // formula is not valid
        navigationBillingFormula.setInternationalArrivalFormula("This is not a formula !");
        // formula is not valid
        navigationBillingFormula.setInternationalOverflightFormula("("+CostFormulaVar.SCHEDCROSSDIST.varInlineName()+"/ 100) * sqrt ()"+ CostFormulaVar.FIRENTRYFEE.varInlineName()+" / 10) * 12");

        List<NavigationBillingFormulaValidationViewModel> navigationBillingFormulaValidationViewModels= navigationBillingFormulaService.validateNavigationBillingFormula(navigationBillingFormula);

        Boolean assertValue=Boolean.TRUE;
        for(NavigationBillingFormulaValidationViewModel navigationBillingFormulaValidationViewModel :navigationBillingFormulaValidationViewModels){
            assertValue=navigationBillingFormulaValidationViewModel.getFormulaValid();
        }
        assertThat(assertValue==Boolean.FALSE);
    }

}
