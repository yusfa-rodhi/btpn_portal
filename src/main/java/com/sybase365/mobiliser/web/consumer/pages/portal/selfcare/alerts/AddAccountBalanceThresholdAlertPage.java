package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.alerts.AddAccountBalanceThresholdAlertPanel;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * This Page is for adding AccountBalanceThreshold Alert.
 * 
 * @author sushil.agrawala
 */
@AuthorizeInstantiation(Constants.PRIV_SHOW_ALERT)
public class AddAccountBalanceThresholdAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddAccountBalanceThresholdAlertPage.class);

    private static long ALERT_TYPE_ID = 2L;

    @SuppressWarnings("unchecked")
    public AddAccountBalanceThresholdAlertPage() {

	super(ALERT_TYPE_ID, ACTION_ADD);

	add(new AddAccountBalanceThresholdAlertPanel("addAlertPanel", this,
		clientLogic, getMobiliserWebSession().getLoggedInCustomer()
			.getCustomerId(), alertType,
		MobileAlertsOptionsPage.class, MobileAlertsPage.class));
    }
}
