'use strict'

import { postWithNoAuth } from "../utils/network.js"

const _baseUrl = "/oAuth"
const _publicUrl = "/public"



const get = async () => {
	const response = await postWithNoAuth(_baseUrl + _publicUrl + '/domains', {})
	return response.data.data
}

const authInfo = async (request) => {
	const response = await postWithNoAuth(_baseUrl + _publicUrl + '/auth-info', request)
	return response.data.data
}

export default {
	get,
	authInfo
}