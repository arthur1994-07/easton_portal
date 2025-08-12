'use strict'


const K_DASHBOARD_MODULE = 'dashboard'
const K_PORTAL_PAGE_MODULE = 'portal_page'


const resolveUrl = (value) => {
	if (value == null) return null;
	return value
		.replace(/\$\{host\}/, window.location.hostname)
		.replace(/\$\{port\}/, window.location.port != '' ? window.location.port : 80)
		.replace(/\$\{protocol\}/, window.location.protocol)
}

export const load = async (item) => {
	var url = resolveUrl(item.url)
	try {
		var module = url == null ? null : await import(/* @vite-ignore */url);
		var handler = module == null || module.eastonAddin == null ? null : module.eastonAddin(item.key);	
	} catch(err) {
		console.error(err)
		handler = null;
	}

	return {
		success: handler != null,
		url : url,
		handler: handler, 
	}
}



const toDashWidgetData = (item) => {
	if (item.register == null) return null
	if (item.getElementKey == null) return null
	if (item.getTitleName == null) return null

	const elementKey = item.getElementKey(K_DASHBOARD_MODULE)
	if (elementKey == null) return null

	return {
		item,
		elementKey,
		register: () => item.register(elementKey)
	}
}

const toPortalTreeData = (item) => {
	if (item.register == null) return null
	if (item.getElementKey == null) return null
	if (item.getItem == null) return null

	const elementKey = item.getElementKey(K_PORTAL_PAGE_MODULE)
	return {
		item: item.getItem(),
		elementKey,
		register: () => item.register(elementKey)
	}
}

const toPortalGroupData = (item) => {
	if (item.getTitle == null) return null
	if (item.key == null) return null
	return {
		icon: item.icon ?? 'mdi-menu',
		getTitle: item.getTitle,
		key: item.key,
		order: item.order ?? 20,
	}
}

export const getDashWidgets = (addin) => {
	if (addin.dashHandler == null) return []

	const item = addin.dashHandler
	if (!Array.isArray(item)) {
		return [toDashWidgetData(item)].filter(s => s != null)
	} 
	return item.map(s => toDashWidgetData(s)).filter(s => s != null)
}

export const getPortalTree = (addin) => {
	if (addin.portalPageHandler == null) return []

	const item = addin.portalPageHandler
	if (!Array.isArray(item)) {
		return [toPortalTreeData(item)].filter(s => s != null)
	} 
	return item.map(s => toPortalTreeData(s)).filter(s => s != null)
}

export const getPortalGroup = (addin) => {
	if (addin.portalGroupHandler == null) return []

	const item = addin.portalGroupHandler
	if (!Array.isArray(item)) {
		return [toPortalGroupData(item)].filter(s => s != null)
	} 
	return item.map(s => toPortalGroupData(s)).filter(s => s != null)
}
