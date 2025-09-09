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

const requestCollectionList = async (id, username, webUrl) => {
	const portalLogin = await webPost(webUrl, "/auth/public/portal-login", { id, username }, null)
	const data = portalLogin.data.data
	const authenticated = data.accessToken != null
	if (!authenticated) throw new Error("Not authenticated")
	// fetch all collections (including protected data)
	
	const response = await webPost(webUrl, "/collection/list", {}, data.accessToken)
	return response.data.data
}

export default {
	requestCollectionList
}