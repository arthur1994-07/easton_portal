'use strict'

import { post } from "../utils/network"

const _baseUrl = "/current-user"

const current = async (request) => {
	const response = await post(_baseUrl + "/get-current", request)
	return response.data.data;
}

const permission = async (request) => {
	const response = await post(_baseUrl + "/permission", request)
	return response.data.data;
}

const logoutUrl = async (request) => {
	const response = await post(_baseUrl + "/logout-url", request)
	return response.data.data;
}


export default {
	current,
	permission,
	logoutUrl
}