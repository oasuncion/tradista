package finance.tradista.security.gcrepo.workflow.guard;

import finance.tradista.flow.model.Guard;
import finance.tradista.security.gcrepo.workflow.mapping.GCRepoTrade;
import jakarta.persistence.Entity;

@Entity
public class IsPartiallyTerminated extends Guard<GCRepoTrade> {

    private static final long serialVersionUID = 1L;

    public IsPartiallyTerminated() {
	setPredicate(trade -> {
	    // Mock value for the moment
	    return true;
	});
    }

}