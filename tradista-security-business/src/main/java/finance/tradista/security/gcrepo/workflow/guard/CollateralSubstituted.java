package finance.tradista.security.gcrepo.workflow.guard;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.flow.model.Guard;
import finance.tradista.security.gcrepo.workflow.mapping.GCRepoTrade;
import jakarta.persistence.Entity;

@Entity
public class CollateralSubstituted extends Guard<GCRepoTrade> {

    private static final long serialVersionUID = 1L;

    public CollateralSubstituted() {
	setPredicate(trade -> {
	    StringBuilder errMsg = new StringBuilder();
	    boolean collateralAdded = trade.getCollateralToAdd() != null && !trade.getCollateralToAdd().isEmpty();
	    boolean collateralRemoved = trade.getCollateralToRemove() != null
		    && !trade.getCollateralToRemove().isEmpty();
	    if (!collateralAdded) {
		errMsg.append(String.format("No collateral has been added.%n"));
	    }
	    if (!collateralRemoved) {
		errMsg.append("No collateral has been removed.");
	    }
	    if (!errMsg.isEmpty()) {
		throw new TradistaBusinessException(errMsg.toString());
	    }
	    return collateralAdded && collateralRemoved;
	});
    }

}