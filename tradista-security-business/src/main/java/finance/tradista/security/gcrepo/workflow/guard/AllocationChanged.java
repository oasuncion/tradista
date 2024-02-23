package finance.tradista.security.gcrepo.workflow.guard;

import finance.tradista.flow.model.Guard;
import finance.tradista.security.gcrepo.workflow.mapping.GCRepoTrade;
import jakarta.persistence.Entity;

@Entity
public class AllocationChanged extends Guard<GCRepoTrade> {

    private static final long serialVersionUID = 1L;

    public AllocationChanged() {
	setPredicate(trade -> {
	    return trade.getCollateralToAdd() != null && !trade.getCollateralToAdd().isEmpty();
	});
    }

}