package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.billpayment;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.WebServiceOperations;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.billpayment.PerformBillPayRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.billpayment.PerformBillPayResponse;
import com.sybase365.mobiliser.money.services.api.ISystemEndpoint;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentPerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is Performing page for bill payment which selects the sub biller code and performs the bill payment.
 * 
 * @author Vikram Gunda
 */
public class BillPaymentPlnPrePaidAmountPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final Logger LOG = LoggerFactory.getLogger(BillPaymentPlnPrePaidAmountPage.class);
	
	private static final long serialVersionUID = 1L;

	@SpringBean(name = "wsBillerTemplate")
	private WebServiceOperations webServiceTemplate;
	
	@SpringBean(name = "systemAuthSystemClient")
	private ISystemEndpoint isystemEndpoint;
	
	protected BillPaymentPerformBean billPayBean;
	
	private Component billAmountField;
	
	private WebMarkupContainer amountContainer;
	private WebMarkupContainer unsoldContainer;
	
	private static final List<String> TYPES = Arrays
			.asList(new String[] { "Amount", "Unsold.1", "Unsold.2" });
	
	//variable to hold radio button values
	private String selected = "Amount";
	
	private String fillBillAmount;
	
	public BillPaymentPlnPrePaidAmountPage(final BillPaymentPerformBean billPayBean) {
		super();
		this.billPayBean = billPayBean;
		initPageComponents();
	}

	
	
	/**
	 * This is the init page components for Bill Pay Perform page.
	 */
	protected void initPageComponents() {
		final Form<BillPaymentPlnPrePaidAmountPage> form = new Form<BillPaymentPlnPrePaidAmountPage>(
			"subBillPaymentForm", new CompoundPropertyModel<BillPaymentPlnPrePaidAmountPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("billPayBean.billerLabel"));
		form.add(new Label("billPayBean.productLabel"));
		form.add(new Label("billPayBean.customerName"));
		form.add(new Label("billPayBean.billNumber"));
		
		// Add the radio button for amount or unsold
		RadioChoice<String> hostingType = (RadioChoice<String>) new RadioChoice<String>(
				"hosting", new PropertyModel<String>(this, "selected"), TYPES).add(new AmountFieldChoiceComponentUpdatingBehavior());
		
		billPayBean.setAmountOrUnsold(BtpnConstants.BILLPAYMENT_AMOUNT);
		
		
		// Add the amount container.
		amountContainer = new WebMarkupContainer("amountContainer");
		amountContainer.add(billAmountField = new AmountTextField<Long>("billPayBean.billAmount", Long.class, false).setRequired(true)
			.add(new ErrorIndicator()));
		amountContainer.setOutputMarkupPlaceholderTag(true);
		amountContainer.setOutputMarkupId(true);
		form.add(amountContainer);
		
		
		/*		form.add(new AmountTextField<Long>("billPayBean.billAmount", Long.class, false).setRequired(true).add(
		*	new ErrorIndicator()));
		 */
		
		// Submit button for form.
		addSubmitButton(form);

		add(form);
		form.add(hostingType);
	}
	
	/**
	 * This is class for displaying unsold list or not based on selection.
	 */
	private class AmountFieldChoiceComponentUpdatingBehavior extends AjaxFormChoiceComponentUpdatingBehavior {

		private static final long serialVersionUID = 1L;

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			final boolean isManual = selected.equals("Amount");
			amountContainer.setVisible(isManual);
			target.addComponent(amountContainer);
		}

	}

	/**
	 * This is used to add submit button.
	 */
	private void addSubmitButton(final Form<BillPaymentPlnPrePaidAmountPage> form) {

		final Button btnSubmit = new Button("btnSubmit") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handlePerformBillPayment();
			};
		};
		form.add(btnSubmit);
	}

	/**
	 * This method is used for performing bill payment.
	 */
	protected void handlePerformBillPayment() {
		try {
			final PerformBillPayRequest request = this.getNewMobiliserRequest(PerformBillPayRequest.class);
			request.setBillpay(ConverterUtils.convertToBillPay(billPayBean));
			request.getBillpay().setMsisdn(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
			final PerformBillPayResponse response = this.billPaymentClient.performBillPay(request);
			if (evaluateConsumerPortalMobiliserResponse(response)) {
				ConverterUtils.convertToBillPaymentPerformBean(response, billPayBean);
				setResponsePage(new BillPaymentConfirmPage(billPayBean));
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("BillPaymentPerformPage:handlePerformBillPayment() ==> Exception occured while performing ", e);
		}
	}

	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("error.perform.billpay", this);
		}
		this.getWebSession().error(message);
	}
}
