package ca.ids.abms.modules.translation;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.RejectedReasons;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Translation.class)
public abstract class TranslationRequired {

    @Before
    public void translationSetup() {
        mockTranslation();
    }

    /**
     * Use to mock Translation static method.
     */
    private void mockTranslation() {
        PowerMockito.mockStatic(Translation.class);
        when(Translation.getLangByToken(anyString())).thenAnswer(i -> i.getArguments()[0].toString());
        when(Translation.getLangByToken(any(RejectedReasons.class))).thenAnswer(i -> i.getArguments()[0].toString());
        when(Translation.getLangByToken(any(ErrorConstants.class))).thenAnswer(i -> i.getArguments()[0].toString());
    }
}
