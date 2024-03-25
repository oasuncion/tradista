package finance.tradista.security.gcrepo.workflow.guard;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.flow.model.Guard;
import finance.tradista.security.gcrepo.service.GCRepoTradeBusinessDelegate;
import finance.tradista.security.gcrepo.workflow.mapping.GCRepoTrade;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
public class IsPartiallyTerminated extends Guard<GCRepoTrade> {

	@Transient
	private GCRepoTradeBusinessDelegate gcRepoTradeBusinessDelegate;

	private static final long serialVersionUID = 1L;

	public IsPartiallyTerminated() {
		gcRepoTradeBusinessDelegate = new GCRepoTradeBusinessDelegate();
		setPredicate(trade -> {
			// Get the previous state of the trade
			finance.tradista.security.gcrepo.model.GCRepoTrade oldTrade = gcRepoTradeBusinessDelegate
					.getGCRepoTradeById(trade.getId());
			// The guard returns true only if the notional has been reduced.
			boolean isPartiallyTerminated = (trade.getCashAmount().compareTo(oldTrade.getAmount()) == -1);
			if (!isPartiallyTerminated) {
				throw new TradistaBusinessException("The cash amount has not been reduced.");
			}
			return isPartiallyTerminated;
		});
	}

}