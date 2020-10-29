package fr.pantheonsorbonne.ufr27.miage.jms.utils;

import java.util.Set;

import org.apache.activemq.artemis.core.security.CheckType;
import org.apache.activemq.artemis.core.security.Role;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;

final class DummyActiveMQSecurityManager implements ActiveMQSecurityManager {
	@Override
	public boolean validateUser(String user, String password) {
		return true;
	}

	@Override
	public boolean validateUserAndRole(String user, String password, Set<Role> roles, CheckType checkType) {
		return true;
	}
}