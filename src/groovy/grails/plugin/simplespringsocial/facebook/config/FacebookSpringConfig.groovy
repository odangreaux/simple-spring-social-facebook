package grails.plugin.simplespringsocial.facebook.config

import javax.inject.Inject
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionFactory
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.connect.support.ConnectionFactoryRegistry
import org.springframework.social.facebook.api.Facebook
import org.springframework.social.facebook.api.impl.FacebookTemplate
import org.springframework.social.facebook.connect.FacebookConnectionFactory

/**
 * Spring configuration for the simple-spring-social-facebook plugin
 */
@Configuration
class FacebookSpringConfig {
	@Inject
	ConnectionFactoryRegistry connectionFactoryRegistry
	@Inject
	ConnectionRepository connectionRepository

	@Bean
	ConnectionFactory facebookConnectionFactory() {
		println "Configuring SimpleSpringSocialFacebook"

		// gets required parameters
		String clientId = ConfigurationHolder.config.grails.plugin.simplespringsocial.facebook.clientId
		String clientSecret = ConfigurationHolder.config.grails.plugin.simplespringsocial.facebook.clientSecret

		// checks the parameters
		assert clientId, "You must configure the Facebook clientId into Config.groovy with the following parameter : grails.plugin.simplespringsocial.facebook.clientId"
		assert clientSecret, "You must configure the Facebook clientId into Config.groovy with the following parameter : grails.plugin.simplespringsocial.facebook.clientSecret"

		// creates and registers a FacebookConnectionFactory
		ConnectionFactory facebookConnectionFactory  = new FacebookConnectionFactory(clientId, clientSecret)
		connectionFactoryRegistry.addConnectionFactory(facebookConnectionFactory)

		return facebookConnectionFactory
	}

	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
	public Facebook facebook() {
		Connection<Facebook> connection = connectionRepository.findPrimaryConnection(Facebook)
		connection != null ? connection.getApi() : new FacebookTemplate()
	}
}
