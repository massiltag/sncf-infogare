package fr.pantheonsorbonne.ufr27.miage;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Address;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Ccinfo;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.FreeTrialPlan;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Invoice;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InvoiceWrapper;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.User;

/**
 * Hello world!
 *
 */
public class RestClientApp

{

	private static FreeTrialPlan getPlan() {
		ObjectFactory factory = new ObjectFactory();
		FreeTrialPlan trial = factory.createFreeTrialPlan();
		User user = factory.createUser();
		user.setFname("Nicolas");
		user.setLname("Herbaut");
		user.setMembershipId(1234);

		Address addresse = factory.createAddress();
		addresse.setCountry("France");
		addresse.setStreetName("rue de Tolbiac");
		addresse.setStreetNumber(90);
		addresse.setZipCode("75014");

		trial.setUser(user);
		trial.setAddress(addresse);
		return trial;
	}

	public static void main(String[] args) throws InterruptedException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");

		FreeTrialPlan plan = getPlan();

		Response resp = target.path("membership").request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.json(getPlan()));

		URI userLocation = null;

		System.out.println("Creating a membership");
		if (resp.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
			System.out.println("Trial Created Successfully");

			userLocation = resp.getLocation();
		} else {

			throw new RuntimeException("failed to create membership" + resp.getStatusInfo().toString());
		}

		if (userLocation != null) {
			System.out.println("changing the address");
			Response userResponse = client.target(userLocation).request().get(Response.class);
			if (userResponse.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
				if (userResponse.hasEntity()) {

					User user = userResponse.readEntity(User.class);

					plan.getAddress().setZipCode("75013");
					Response addressUpdateResponse = client.target(userLocation).path("address").request()
							.put(Entity.json(plan.getAddress()));

					if (addressUpdateResponse.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
						System.out.println("Address Sucessfully updated checking is user has been updated");
						Response respGetAddress = client.target(userLocation).path("address").request()
								.get(Response.class);

						if (respGetAddress.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
							Address address = respGetAddress.readEntity(Address.class);
							if (!address.getZipCode().equals("75013")) {
								throw new RuntimeException("address was't updated");
							} else {
								System.out.println("address updated successfully");

								Ccinfo ccinfo = new Ccinfo();
								ccinfo.setCcv(123);
								ccinfo.setNumber("1212126126216");
								ccinfo.setValidityDate("10/22");
								Response paymentResponse = target.path("payment").path("" + user.getId()).request()
										.post(Entity.xml(ccinfo));
								if (!paymentResponse.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
									throw new RuntimeException("failed to pay");
								}
								System.out.println("Payment sent");

								allpaid: while (true) {
									boolean allPaid = true;
									Response invoicesResp = target.path("invoice").path("" + user.getId()).request()
											.get();

									if (invoicesResp.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
										InvoiceWrapper invoices = invoicesResp.readEntity(InvoiceWrapper.class);
										for (Invoice invoice : invoices.getInvoices()) {
											if (!invoice.isPaid()) {
												allPaid = false;
												break;
											}
											System.out.println("contract " + invoice.getContractId() + " from "
													+ invoice.getDate().toString() + " " + invoice.isPaid());

										}

									} else {
										throw new RuntimeException("failed to load invoices");
									}

									if (!allPaid) {
										System.out.println("all invoices are not paid");
									} else {
										System.out.println("all invoices are paid");
										break allpaid;
									}
									Thread.sleep(1000);
								}

							}
						} else {
							throw new RuntimeException("failed to fetch updated address");
						}

					} else {

						throw new RuntimeException("failed to update address" + addressUpdateResponse.toString());
					}

				}

			} else {
				throw new RuntimeException("failed to get user after creation");

			}
		} else {

			throw new RuntimeException("failed to create user" + resp.getStatusInfo().toString());
		}

	}
}
