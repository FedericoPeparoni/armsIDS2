package ca.ids.abms.modules.exemptions.charges.methods;

import ca.ids.abms.modules.exemptions.charges.ExemptionCharge;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

public class LargestExemptionChargeMethodTest {

    private LargestExemptionChargeMethod method;

    @Before
    public void setup() {
        method = new LargestExemptionChargeMethod();
    }

    @Test
    public void resolveTest() {

        // CASE 1: throw IllegalParameterException when model is undefined
        try {
            // noinspection ConstantConditions
            method.resolve(null);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException ignored) {}


        // CASE 2: when no exemption to resolve, return null
        ExemptionChargeMethodResult result = method.resolve(new ExemptionChargeMethodModel.Builder()
            .chargeValue(123.0).exemptionCharges(null).build());
        assertThat(result).isNull();

        result = method.resolve(new ExemptionChargeMethodModel.Builder()
            .chargeValue(123.0).exemptionCharges(Collections.emptyList()).build());
        assertThat(result).isNull();


        // CASE 3: largest exemption applied to charge value, all notes should be applied with not null charge exemption
        Collection<ExemptionCharge> exemptionCharges = new ArrayList<>();
        exemptionCharges.add(new ExemptionCharge(null, "MOCK NO EXEMPTION"));
        exemptionCharges.add(new ExemptionCharge(0.0, "MOCK SMALLEST EXEMPTION"));
        exemptionCharges.add(new ExemptionCharge(50.0, "MOCK MIDDLE EXEMPTION"));
        exemptionCharges.add(new ExemptionCharge(100.0, "MOCK LARGEST EXEMPTION"));

        result = method.resolve(new ExemptionChargeMethodModel.Builder()
            .chargeValue(123.0).exemptionCharges(exemptionCharges).build());

        assertThat(result.getAppliedCharge()).isEqualTo(0.0);
        assertThat(result.getExemptCharge()).isEqualTo(123.0);
        assertThat(result.getExemptNotes().size()).isEqualTo(3);
        assertThat(result.getExemptNotes()).containsExactlyInAnyOrder(
            "MOCK SMALLEST EXEMPTION", "MOCK MIDDLE EXEMPTION", "MOCK LARGEST EXEMPTION");
    }
}
