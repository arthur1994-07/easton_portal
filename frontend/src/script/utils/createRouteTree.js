'use strict'

import { routeList } from "../utils/route_tree";
import { getPathParent } from "../core/RouteTree";


export const constructRenderDataFromItem = (data, subItems) => ({
	subItems : subItems,
	path : data?.fullPath,
	icon : data?.icon,
	displayText : data?.title,
	order: data?.order == undefined ? Number.MAX_SAFE_INTEGER : data?.order,
	key: data?.title,
	showMenu : false,
	isActive : false,
})

export const permissionAllow = (item, permissions) => {
	if (item.permission == null) return true
	return item.permission.some(s => permissions.find(t => s == t) != null)
}

export const constructRouterTree = (route, permissions) => {
	let root = routeList.find(s => s.meta.fullPath == getPathParent(route.path, 0))
	let noSubTree = root.children.filter(s => s.meta.group == undefined)
		.filter(s => permissionAllow(s.meta, permissions))
		.map(s => constructRenderDataFromItem(s.meta, []))
	let	subTree = root.meta?.groups == null ? [] : root.meta?.groups.map(s => {
		var subList = root.children
			.filter(t => s.key == t.meta.group)
			.filter(s => permissionAllow(s.meta, permissions))
			.map(t => constructRenderDataFromItem(t.meta, []))
			.sort((a, b) => { return a.order < b.order ? -1 : (a.order > b.order ? 1 : 0) })
		return constructRenderDataFromItem(s, subList)
	}).filter(s => s.subItems.length != 0)
	return [...noSubTree, ...subTree]
		.sort((a, b) => { return a.order < b.order ? -1 : (a.order > b.order ? 1 : 0) })
}

export const constructGroupItemState = (items) => {
	var state = {}
	items.filter(s => s.path == null).forEach((s) => s[s.key] = false)
	return state
}