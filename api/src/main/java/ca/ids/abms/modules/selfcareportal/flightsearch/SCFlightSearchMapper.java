package ca.ids.abms.modules.selfcareportal.flightsearch;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public abstract class SCFlightSearchMapper {

    @Autowired
    private CurrencyUtils currencyUtils;

    @Autowired
    private TransactionService transactionService;

    public abstract List<SCFlightMovementViewModel> toViewModel(Iterable<FlightMovement> items);

    @Mapping(target = "accountName", source = "account.name")
    @Mapping(target = "accountId", source = "account.id")
    public abstract SCFlightMovementViewModel toViewModel(FlightMovement item);

    @Mapping(target = "accountName", source = "account.name")
    public abstract SCFlightMovementCsvExportModel toCsvModel(FlightMovement item);

    public abstract List<SCFlightMovementCsvExportModel> toCsvModel(Iterable<FlightMovement> items);

    @AfterMapping
    void resolveAccountBalance (@MappingTarget List<SCFlightMovementViewModel> result) {
        final Currency usdCurrency = currencyUtils.getCurrencyUSD();
        Integer accountId = null;
        double balance = 0d;
        for (SCFlightMovementViewModel viewModel: result) {
            Integer viewModelAccountId = viewModel.getAccountId();
            if (viewModelAccountId == null) {
                continue;
            }
            if (!viewModelAccountId.equals(accountId)) {
                accountId = viewModelAccountId;
                balance = getAccountBalance(accountId, usdCurrency.getId());
                viewModel.setAssociatedAccountUsdBalance(balance);
            } else {
                viewModel.setAssociatedAccountUsdBalance(balance);
            }
        }
    }

    private double getAccountBalance (int accountId, int currencyId) {
        double balance = 0d;

        final Transaction lastTransaction = transactionService.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(accountId, currencyId);
        if (lastTransaction != null) {
            balance = lastTransaction.getBalance();
        }

        return balance;
    }
}
