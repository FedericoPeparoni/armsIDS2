package ca.ids.abms.plugins.caab.sage.modules.distributioncode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DistributionCodeService {

    private final DistributionCodeRepository distributionCodeRepository;

    public DistributionCodeService(final DistributionCodeRepository distributionCodeRepository) {
        this.distributionCodeRepository = distributionCodeRepository;
    }

    /**
     * Retrieve single distribution code by sage charge and operation codes.
     *
     * @param chargeCode sage charge code
     * @param operationCode sage operation code
     * @return distribution code
     */
    @Transactional(readOnly = true)
    public DistributionCode findByChargeAndOperationCode(final String chargeCode, final String operationCode) {
        if (chargeCode != null && !chargeCode.isEmpty() && operationCode != null && !operationCode.isEmpty())
            return this.distributionCodeRepository.findByChargeAndOperationCode(chargeCode, operationCode);
        else
            return null;
    }
}
