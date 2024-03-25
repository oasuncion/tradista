package finance.tradista.security.gcrepo.workflow.guard;

import java.time.LocalDate;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.flow.model.Guard;
import finance.tradista.security.gcrepo.workflow.mapping.GCRepoTrade;
import jakarta.persistence.Entity;

@Entity
public class TradeEndDateReached extends Guard<GCRepoTrade> {

	private static final long serialVersionUID = 5851944303197124273L;

	public TradeEndDateReached() {
		setPredicate(trade -> {
			boolean isTradeEndDateReached = !LocalDate.now().isBefore(trade.getEndDate());
			if (!isTradeEndDateReached) {
				throw new TradistaBusinessException("The trade end date has not been reached.");
			}
			return isTradeEndDateReached;
		});
	}

}