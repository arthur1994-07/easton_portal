'use strict'

import { post } from "../utils/network"

const _baseUrl = "/user"


const list = async (request) => {
	const response = await post(_baseUrl + "/list", request)
	return response.data.data;
}


const remove = async (request) => {
	const response = await post(_baseUrl + "/remove", request)
	return response.data.data;
}

const changeActive = async (request) => {
	const response = await post(_baseUrl + "/change-active", request)
	return response.data.data;
}

const changeRole = async (request) => {
	const response = await post(_baseUrl + "/change-role", request)
	return response.data.data;
}

export default {
	list,
	remove,
	changeActive,
	changeRole,
}