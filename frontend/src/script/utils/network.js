'use strict'

import {config} from '../../config/config.js'
import router from '../plugins/vuerouter.js'
import store from '../plugins/vuexstore.js'
import auth from "../services/AuthService.js"
import axios from 'axios'

const tokenHandler = () => {
	const tokenKey = "token"
	const expirationKey = "expiration"
	let accessToken = null
	let tokenExpiration = null

	const loadTokenData = () => {
		accessToken = window.sessionStorage.getItem(tokenKey)
		tokenExpiration =  Number(window.sessionStorage.getItem(expirationKey))
		window.sessionStorage.removeItem(tokenKey)
		window.sessionStorage.removeItem(expirationKey)
	}

	window.onbeforeunload = () => {
		window.sessionStorage.setItem(tokenKey, accessToken)
		window.sessionStorage.setItem(expirationKey, tokenExpiration)
	}
	loadTokenData()
	return {
		getToken: () => {
			return {
				token : accessToken,
				expiration : tokenExpiration == null ? new Date() : new Date(tokenExpiration),
			}
		},
		setToken: (token, expiration) => {
			accessToken = token
			tokenExpiration = expiration
		},
		refreshAccessToken: async () => await auth.refreshAccessToken(),
	}
}

const logOutHandler = () => {
	let logOutTimer = null

	return {
		set: (expiration) => {
			clearTimeout(logOutTimer)
			if (expiration == null) return

			let timeout = new Date(expiration).getTime() - new Date().getTime()
			if (timeout <= 0) return

			logOutTimer = setTimeout(() => {
				auth.signOut()
				router.push('/login')
				store.dispatch('ui/notify', {
					success: false,
					text: "Logged out: token expired"
				})
			}, timeout);
		}
	}
}

const networkModule = (url, tokenHandler) => {
	const _expiredSecond = 180	
	return {	
		post : async (path, data, headers = null, specific) => {
			let tokenData = tokenHandler?.getToken()
			if (!specific?.noAuth && !specific?.noRefresh && tokenData?.token != null &&
				tokenData?.expiration.getTime() < new Date().getTime() + _expiredSecond * 1000) {
				await tokenHandler.refreshAccessToken()
			}
	
			if (headers == null) headers = {}
			if (!specific?.noAuth && tokenData?.token != null) {
				headers.Authorization = `Bearer ${tokenData.token}`
			}
	
			let config = {headers}
			if (specific?.responseType) {
				config.responseType = specific.responseType
			}
			try {
				return await axios.post(url + path, data, config)
			} catch(error) {
				var errMsg = error?.response?.data?.errorMessage || error?.message || `Calling url ${path} has problem`
				throw new Error(errMsg)
			}
		}	
	}
}

const logOutInstance = logOutHandler()
const tokenHandlerInstance = tokenHandler()
const network = networkModule(config.$api_url, tokenHandlerInstance)
		

export const post = async (url, request, headers = null, specific) => {
	return await network.post(url, request, headers, specific)
}

export const postWithNoAuth = async (url, request) => {
	return await post(url, request, null, { noAuth : true })
}

export const setToken = (token, expiration) => {
	tokenHandlerInstance.setToken(token, expiration)
	logOutInstance.set(expiration)
}

export const isAuthenticated = () => {
	let tokenData = tokenHandlerInstance.getToken()
	return tokenData?.token != null && tokenData?.expiration > new Date()
}
