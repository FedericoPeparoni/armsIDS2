package ca.ids.abms.modules.translation;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.RejectedReasons;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TranslationTest extends TranslationRequired {

    @Test
    public void getLangByTokenTest() {

        String result = Translation.getLangByToken(RejectedReasons.VALIDATION_ERROR);
        assertThat(result).isEqualTo(RejectedReasons.VALIDATION_ERROR.toString());

        result = Translation.getLangByToken(ErrorConstants.ERR_VALIDATION);
        assertThat(result).isEqualTo(ErrorConstants.ERR_VALIDATION.toString());

        result = Translation.getLangByToken("1234abcd_THISVALUEDOESNOTEXISTS_wxyz5678");
        assertThat(result).isEqualTo("1234abcd_THISVALUEDOESNOTEXISTS_wxyz5678");
    }
}
