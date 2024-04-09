package finance.tradista.security.gcrepo.workflow.condition;

import java.math.BigDecimal;
import java.time.LocalDate;

import finance.tradista.flow.model.Condition;
import finance.tradista.security.gcrepo.service.GCRepoPricerBusinessDelegate;
import finance.tradista.security.gcrepo.workflow.mapping.GCRepoTrade;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
public class IsAllocated extends Condition<GCRepoTrade> {

	private static final long serialVersionUID = -1790346124051863865L;

	@Transient
	private GCRepoPricerBusinessDelegate gcRepoPricerBusinessDelegate;

	public IsAllocated() {
		gcRepoPricerBusinessDelegate = new GCRepoPricerBusinessDelegate();
		setFunction(trade -> {
			// Calculate the total MTM value of the collateral
			BigDecimal mtm = gcRepoPricerBusinessDelegate
					.getCurrentCollateralMarketToMarket(trade.getOriginalGCRepoTrade());

			if (trade.getCollateralToAdd() != null && !trade.getCollateralToAdd().isEmpty()) {
				mtm = mtm.add(gcRepoPricerBusinessDelegate.getCollateralMarketToMarket(trade.getCollateralToAdd(),
						trade.getBook().getProcessingOrg(), LocalDate.now()));
			}

			// Calculate the exposure (required collateral)
			BigDecimal exposure = gcRepoPricerBusinessDelegate.getCurrentExposure(trade.getOriginalGCRepoTrade());

			// Compare the collateral value and the required collateral

			return exposure.compareTo(mtm) != -1 ? 1 : 2;

		});
	}

}