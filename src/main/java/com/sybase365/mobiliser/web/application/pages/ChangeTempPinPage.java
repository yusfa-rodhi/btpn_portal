package com.sybase365.mobiliser.web.application.pages;

import java.util.Map;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.util.tools.wicketutils.security.ChangeTempPinForm;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;

public class ChangeTempPinPage extends BaseLoginPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ChangeTempPinPage.class);

    private Map<String, String> param;

    public ChangeTempPinPage() {
	super();
	LOG.info("Created new ChangeTempPinPage");
    }

    public ChangeTempPinPage(final PageParameters parameters) {
	super(parameters);
	LOG.info("Created new ChangeTempPinPage");
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	Customer customer = getWebSession().getLoggedInCustomer();
	Form<?> form = new ChangeTempPinForm("changeTempPinForm",
		customer.getUsername(), customer.getTempPassword());
	form.add(new FeedbackPanel("errorMessages"));
	add(form);
    }

}
