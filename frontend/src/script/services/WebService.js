'use strict'

import { post } from "../utils/network.js"

const _baseUrl = "/web"

const getInfo = async (request) => {
	const response = await post(_baseUrl + '/get-info', request)
	return response.data.data
}


export default {
	getInfo
}