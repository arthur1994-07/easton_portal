'use strict'

import { post } from "../utils/network"

const _baseUrl = "/role"

const list = async (request) => {
	const response = await post(_baseUrl + "/list", request)
	return response.data.data;
}

const add = async (request) => {
	const response = await post(_baseUrl + "/add", request)
	return response.data.data;
}

const remove = async (request) => {
	const response = await post(_baseUrl + "/delete", request)
	return response.data.data;
}

const rename = async (request) => {
	const response = await post(_baseUrl + "/rename", request)
	return response.data.data;
}

const updateRights = async (request) => {
	const response = await post(_baseUrl + "/right/update", request)
	return response.data.data;
}

const rightList = async (request) => {
	const response = await post(_baseUrl + "/right/list", request)
	return response.data.data;
}

export default {
	list,
	rightList,
	add,
	remove,
	rename,
	updateRights,
}