'use strict'

import { post } from "../utils/network"

const _baseUrl = "/quotation"

const create = async (request) => {
	const response = await post(_baseUrl + "/create", request)
	return response.data.data
}


const list = async (request) => {
	const response = await post(_baseUrl + "/list", request)
	return response.data.data
}


export default {
	create,
	list,
}