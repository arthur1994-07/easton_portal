'use strict'

import { post } from "../utils/network"

const _baseUrl = "/notification"

const getData = async (request) => {
	const response = await post(_baseUrl + "/get-data", request)
	return response.data.data;
}

export default {
	getData
}
