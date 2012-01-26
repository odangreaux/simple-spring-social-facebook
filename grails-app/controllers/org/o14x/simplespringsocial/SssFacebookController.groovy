package org.o14x.simplespringsocial

import org.springframework.social.facebook.api.Facebook
import org.springframework.social.facebook.api.impl.FacebookTemplate
import org.springframework.social.facebook.connect.FacebookConnectionFactory

import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.connect.support.ConnectionFactoryRegistry
import org.springframework.social.oauth2.AccessGrant
import org.springframework.social.oauth2.GrantType
import org.springframework.social.oauth2.OAuth2Operations
import org.springframework.social.oauth2.OAuth2Parameters

/**
 * Controller for Facebook related operations.
 */
class SssFacebookController {
	ConnectionFactoryRegistry connectionFactoryRegistry
	ConnectionRepository connectionRepository

    def index() {
		redirect(action: "login")
	}

	/**
	 * Login action
	 */
	def login = {
		// Gets a OAuth2Operations object
		FacebookConnectionFactory connectionFactory = (FacebookConnectionFactory) connectionFactoryRegistry.getConnectionFactory("facebook");
		OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();

		// prepares the oauth2 parameters
		OAuth2Parameters oAuth2Parameters = new OAuth2Parameters();
		oAuth2Parameters.setRedirectUri(createLink(action: "connected", absolute: true).toString());
		oAuth2Parameters.setScope("user_about_me");

		// builds the oauth2 authorize URL
		String authorizeUrl = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, oAuth2Parameters);

		// redirects to the oauth2 authorize URL
		redirect(url: authorizeUrl)
	}

	/**
	 * Callback action called by the oauth2 provider
	 */
	def connected = {
		// gets the authorizationCode from the callback URL
		def authorizationCode = params.code

		// creates a Spring social Connection object
		FacebookConnectionFactory connectionFactory = (FacebookConnectionFactory) connectionFactoryRegistry.getConnectionFactory("facebook");
		OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
		AccessGrant accessGrant = oauthOperations.exchangeForAccess(authorizationCode, createLink(action: "connected", absolute: true).toString(), null);
		Connection<Facebook> connection = connectionFactory.createConnection(accessGrant);

		// registers the connection into the connectionRepository
		connectionRepository.addConnection(connection)

		// redirects to the user defined controller and action
		def redirectController = grailsApplication.config.grails.plugins.simplespringsocial.facebook.loginSuccess.controller
		def redirectAction = grailsApplication.config.grails.plugins.simplespringsocial.facebook.loginSuccess.action
		redirect(controller: redirectController, action: redirectAction)
	}
}
