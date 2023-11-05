package finance.tradista.security.gcrepo.model;

import java.util.Set;

import finance.tradista.core.common.model.Id;
import finance.tradista.core.common.model.TradistaModelUtil;
import finance.tradista.core.common.model.TradistaObject;
import finance.tradista.security.common.model.Security;

public class GCBasket extends TradistaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String name;

	private Set<Security> securities;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public Set<Security> getSecurities() {
		return (Set<Security>) TradistaModelUtil.deepCopy(securities);
	}

	public void setSecurities(Set<Security> securities) {
		this.securities = securities;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GCBasket clone() {
		GCBasket gcBasket = (GCBasket) super.clone();
		gcBasket.setSecurities((Set<Security>) TradistaModelUtil.deepCopy(securities));
		return gcBasket;
	}

	public String toString() {
		return name;
	}
}