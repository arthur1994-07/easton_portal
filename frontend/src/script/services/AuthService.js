'use strict'

import { postWithNoAuth, setToken, isAuthenticated } from "../utils/network.js"
import { config } from '../../config/config.js'
import oAuth from "./OAuthService.js"

const _baseUrl = "/auth"
const _publicUrl = "/public"
const _redirectUriPrefix = `${config.$redirect_host}`


const generateOAuthState = () => {
	const validChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
	let array = new Uint8Array(40)
	window.crypto.getRandomValues(array)
	array = array.map(x => validChars.charCodeAt(x % validChars.length))
	return String.fromCharCode.apply(null, array)
}

const redirectToOAuthServer = async (id, url, state) => {
	const info = await oAuth.authInfo({id})
	if (!info || !info.clientId) throw Error("Client id not found")

	const options = {
		response_type: "code",
		client_id: info.clientId,
		redirect_uri: _redirectUriPrefix + url,
		scope: "openid profile email",
		state: state
	}
	const params = new URLSearchParams(options).toString()
	return `${info.authUrl}?${params}`
}

const authenticateWithOAuth = async (id, code, uriSuffix) => {
	const response = await postWithNoAuth(_baseUrl + _publicUrl + "/oAuth/authenticate", 
		{ id, code, redirectUri: _redirectUriPrefix + uriSuffix })
	const data = response.data.data
	const authenticated = data.accessToken != null
	if (authenticated) {
		setToken(data.accessToken, data.expiration)
		return { authenticated, idToken : data.idToken }
	} else {
		throw Error("User cannot login. Please contact your system administrator")
	}
}

const redirectLogout = async (logoutUrl, id_token, url) => {
	setToken(null, null);
	const options = {
		id_token_hint: id_token,
		post_logout_redirect_uri: _redirectUriPrefix + url,
	}
	const params = new URLSearchParams(options).toString()
	window.location.replace(`${logoutUrl}?${params}`)
}



export default {
	generateOAuthState,
	authenticated: () => isAuthenticated(),
	signOut: () => setToken(null, null),
	oAuth: {
		redirect: redirectToOAuthServer,
		signIn: authenticateWithOAuth,
		signOut: redirectLogout,
	}
}