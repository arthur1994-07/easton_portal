'use strict'
import axios from 'axios'


const webPost = async (url, path, data, token) => {
	const headers = {}
	if (token != null) {
		headers.Authorization = `Bearer ${token}`
	}
	let config = {headers}
	try {
		return await axios.post(url + path, data, config)
	} catch(error) {
		var errMsg = error?.response?.data?.errorMessage || error?.message || `Calling url ${path} has problem`
		throw new Error(errMsg)
	}
}

const webAuthenticate = async (id, username, webUrl) => {
	const portalLogin = await webPost(webUrl, "/auth/public/portal-login", { id, username }, null)
	const data = portalLogin.data.data
	const authenticated = data.accessToken != null
	if (!authenticated) throw new Error("Not authenticated")
	return data.accessToken
}

const list = async (accessToken, webUrl) => {
	const response = await webPost(webUrl, "/collection/list", {}, accessToken)
	return response.data.data
}

const getImagesPage = async (webUrl, request, accessToken) => {
	const response = await webPost(webUrl, "/collection/images-page-all", {
		pageIndex: request.pageIndex,
		itemsPerPage: request.itemsPerPage,
	}
	, accessToken)
	return response.data.data
}

const findImageById = async (request, webUrl) => {
	const response = await webPost(webUrl, "/image/public/image-by-id", request, null)
	return response.data.data
}

const create = async (request, accessToken, webUrl) => {
	if (request == null || accessToken == null) return
	const response = await webPost(webUrl, "/collection/create",
		{ 
			name: request.name,
			year: request.year,
			isProtected: request.isProtected,
			image: request.image
		}
		, accessToken)
	return response.data.data
}

const remove = async(request, accessToken, webUrl) => {
	if (request == null || accessToken == null) return
	const response = await webPost(webUrl, "/collection/remove",
		{ id: request.id }, accessToken) 
	return response.data.data
}

const update = async (request, accessToken, webUrl) => {
	if (request == null || accessToken == null) return
	const response = await webPost(webUrl, "/image/update",
		{
			id: request.id,
			image: request.image,
			format: request.format
		}
		, accessToken)
	return response.data.data
}



export default {
	webAuthenticate,
	list,
	create,
	update, 
	remove,
	findImageById,
	getImagesPage
}