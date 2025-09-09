'use strict'

import { post } from "../utils/network"

const _baseUrl = "/request"

const create = async (request) => {
	const response = await post(_baseUrl + "/create", request)
	return response.data.data
}

const findQuotationRequest = async (request) => {
	const response = await post(_baseUrl + "/get-untreated-request", request)
	return response.data.data
}

const findQuotation = async (request) => {
	const response = await post(_baseUrl + "/get-treated-request", request)
	return response.data.data
}

export default {
	create,
	findQuotation,
	findQuotationRequest
}