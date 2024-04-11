package finance.tradista.security.gcrepo.workflow.guard;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.flow.model.Guard;
import finance.tradista.security.gcrepo.workflow.mapping.GCRepoTrade;
import jakarta.persistence.Entity;

@Entity
public class CollateralAdded extends Guard<GCRepoTrade> {

	private static final long serialVersionUID = -4754668279484977088L;

	public CollateralAdded() {
		setPredicate(trade -> {
			boolean collateralAdded = trade.getCollateralToAdd() != null && !trade.getCollateralToAdd().isEmpty();
			if (!collateralAdded) {
				throw new TradistaBusinessException("No collateral has been added.");
			}
			return collateralAdded;
		});
	}

}