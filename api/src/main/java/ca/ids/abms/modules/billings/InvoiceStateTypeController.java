package ca.ids.abms.modules.billings;

import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/invoice-state-types")
public class InvoiceStateTypeController {

    private final InvoicesApprovalWorkflow invoicesApprovalWorkflow;

    public InvoiceStateTypeController(final InvoicesApprovalWorkflow invoicesApprovalWorkflow) {
        this.invoicesApprovalWorkflow = invoicesApprovalWorkflow;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<InvoiceStateType> getAllInvoiceStateTypes() {
        return invoicesApprovalWorkflow.getLedgerStatesList();
    }
}
