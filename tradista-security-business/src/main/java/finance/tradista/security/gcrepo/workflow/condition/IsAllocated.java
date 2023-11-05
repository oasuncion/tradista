package finance.tradista.security.gcrepo.workflow.condition;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.index.model.Index;
import finance.tradista.core.marketdata.model.QuoteType;
import finance.tradista.core.marketdata.model.QuoteValue;
import finance.tradista.core.marketdata.service.QuoteBusinessDelegate;
import finance.tradista.core.pricing.pricer.PricingParameter;
import finance.tradista.core.pricing.service.PricerBusinessDelegate;
import finance.tradista.core.pricing.util.PricerUtil;
import finance.tradista.core.transfer.service.TransferBusinessDelegate;
import finance.tradista.flow.model.Condition;
import finance.tradista.security.bond.model.Bond;
import finance.tradista.security.common.model.Security;
import finance.tradista.security.gcrepo.service.GCRepoTradeBusinessDelegate;
import finance.tradista.security.gcrepo.workflow.mapping.GCRepoTrade;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
public class IsAllocated extends Condition<GCRepoTrade> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected static final String DEFAULT_PRICING_PARAMETER = "DefautPP";

    @Transient
    private TransferBusinessDelegate transferBusinessDelegate;

    @Transient
    private QuoteBusinessDelegate quoteBusinessDelegate;

    @Transient
    private PricerBusinessDelegate pricerBusinessDelegate;
    
    @Transient
    private GCRepoTradeBusinessDelegate gcRepoTradeBusinessDelegate;

    public IsAllocated() {
	transferBusinessDelegate = new TransferBusinessDelegate();
	quoteBusinessDelegate = new QuoteBusinessDelegate();
	pricerBusinessDelegate = new PricerBusinessDelegate();
	gcRepoTradeBusinessDelegate = new GCRepoTradeBusinessDelegate();
	setFunction(trade -> {
	    // 1. Get the current collateral

	    Map<Security, BigDecimal> securities = gcRepoTradeBusinessDelegate.getAllocatedCollateral(trade.getId());
	    BigDecimal mtm = BigDecimal.ZERO;
	    BigDecimal rate;
	    BigDecimal exposure;
	    PricingParameter pp = pricerBusinessDelegate.getPricingParameterByNameAndPoId(DEFAULT_PRICING_PARAMETER,
		    trade.getBook().getProcessingOrg().getId());


	    // Calculate the total MTM value of the collateral

	    if (securities != null) {
		for (Map.Entry<Security, BigDecimal> entry : securities.entrySet()) {
		    String quoteName = entry.getKey().getProductType() + "." + entry.getKey().getIsin() + "."
			    + entry.getKey().getExchange();
		    QuoteValue qv = quoteBusinessDelegate.getQuoteValueByQuoteSetIdQuoteNameTypeAndDate(
			    pp.getQuoteSet().getId(), quoteName,
			    entry.getKey().getProductType().equals(Bond.BOND) ? QuoteType.BOND_PRICE
				    : QuoteType.EQUITY_PRICE,
			    LocalDate.now());
		    if (qv == null) {
			throw new TradistaBusinessException(
				String.format("The security price %s could not be found on quote set %s as of %tD",
					quoteName, pp.getQuoteSet(), LocalDate.now()));
		    }
		    BigDecimal price = qv.getClose() != null ? qv.getClose() : qv.getLast();
		    if (price == null) {
			throw new TradistaBusinessException(String.format(
				"The closing or last price of the product %s could not be found on quote set %s as of %tD",
				entry.getKey(), pp.getQuoteSet(), LocalDate.now()));
		    }
		    mtm = mtm.add(price.multiply(entry.getValue()));
		}
	    }

	    // Calculate the required exposure

	    if (trade.isFixedRepoRate()) {
		rate = trade.getRepoRate();
	    } else {
		String quoteName = Index.INDEX + "." + trade.getIndex() + "." + trade.getIndexTenor();
		QuoteValue qv = quoteBusinessDelegate.getQuoteValueByQuoteSetIdQuoteNameTypeAndDate(
			pp.getQuoteSet().getId(), quoteName, QuoteType.INTEREST_RATE, LocalDate.now());
		if (qv == null) {
		    throw new TradistaBusinessException(
			    String.format("The index %s could not be found on quote set %s as of %tD", quoteName,
				    pp.getQuoteSet(), LocalDate.now()));
		}
		// the index is expected to be defined as quote closing value.
		rate = qv.getClose();
		if (rate == null) {
		    throw new TradistaBusinessException(
			    String.format("The index %s (closing value) could not be found on quote set %s as of %tD",
				    quoteName, pp.getQuoteSet(), LocalDate.now()));
		}
	    }

	    exposure = trade.getCashAmount()
		    .multiply(rate.multiply(PricerUtil.daysToYear(LocalDate.now(), trade.getEndDate())));

	    // Apply the margin rate (by convention, margin rate is noted as follows: 105
	    // for 5%)
	    BigDecimal marginRate = trade.getMarginRate().divide(BigDecimal.valueOf(100));
	    exposure = exposure.multiply(marginRate);

	    // Compare the collateral value and the required collateral

	    return exposure.compareTo(mtm) != -1 ? 1 : 2;

	});
    }

}