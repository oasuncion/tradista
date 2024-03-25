package finance.tradista.security.gcrepo.workflow.condition;

import java.math.BigDecimal;

import finance.tradista.flow.model.Condition;
import finance.tradista.security.gcrepo.service.GCRepoTradeBusinessDelegate;
import finance.tradista.security.gcrepo.workflow.mapping.GCRepoTrade;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
public class IsAllocated extends Condition<GCRepoTrade> {

    private static final long serialVersionUID = 1L;

    @Transient
    private GCRepoTradeBusinessDelegate gcRepoTradeBusinessDelegate;

    public IsAllocated() {
	gcRepoTradeBusinessDelegate = new GCRepoTradeBusinessDelegate();
	setFunction(trade -> {
	    // Calculate the total MTM value of the collateral
	    BigDecimal mtm = gcRepoTradeBusinessDelegate.getCollateralMarketToMarket(trade.getId());

	    if (trade.getCollateralToAdd() != null && !trade.getCollateralToAdd().isEmpty()) {
		mtm = mtm.add(gcRepoTradeBusinessDelegate.getCollateralMarketToMarket(trade.getCollateralToAdd(),
			trade.getBook().getProcessingOrg().getId()));
	    }

	    // Calculate the exposure (required collateral)
	    BigDecimal exposure = gcRepoTradeBusinessDelegate.getExposure(trade.getId());

	    // Compare the collateral value and the required collateral

	    return exposure.compareTo(mtm) != -1 ? 1 : 2;

	});
    }

}