'use strict'

import { resolveFullPath, getRouteTree, getRouteList } from '../core/RouteTree.js'
import * as PermissionType from '../enums/PermissionType.js'

// group property
const _settingsGrp = 'settings'
// const _organizationGrp = 'organization'
// const _userGrp = 'user'
const _collectionGrp = 'collection'
const _managementGrp = 'management'

const createSettingGroup = () => ({
	icon: "mdi-cog",
	title: "Settings",
	key: _settingsGrp,
	order: 1
})

const createCollectionGroup = () => ({
	icon: "mdi-shopping",
	title: "Collections",
	key: _collectionGrp,
	order: 2,
})

const createManagementGroup = () => ({
	icon: "mdi-folder-account",
	title: "Management",
	key: _managementGrp,
	order: 3,
})

// page property

const createLoginView = () => ({
	path: "/login",
	name: "login",
	component:  () => import('../../views/LoginView.vue'),
	meta: { bypassAuth: true, }
})

const createPortalView = (groups, children) => ({
	path: "/",
	name: "portal",
	component:  () => import('../../views/PortalView.vue'),
	meta: { groups: groups.filter(s => s != null), },
	children: children.filter(s => s != null),
})

const createDashboardView = () => ({
	path: "/",
	name: "portal-empty",
	component: () => import('../../views/DashboardView.vue'),
	meta: {
		icon: 'mdi-tablet-dashboard',
		title: "Home Page",
		order: 0,
	}
})


const createCollectionListView = () => ({
	path: "collection-list",
	name: "Collection list",
	component : () => import("../../views/EditCollectionListView.vue"),
	meta: {
		icon: 'mdi-gift',
		title: 'Collection List',
		group: _collectionGrp,
		order: 1
	}
})

const createQuotationRequestView = () => ({
	path: "quotation-request",
	name: "Quotation Request",
	component : () => import("../../views/EditQuotationRequestView.vue"),
	meta: {
		icon: 'mdi-file-document',
		title: 'Quotation Request',
		permission: [PermissionType.QUOTATION_USER],
		group: _managementGrp,
		order: 2
	}
})

const createQuotationStatusView = () => ({
	path: "quotation-status",
	name: "Quotation Status",
	component : () => import("../../views/EditQuotationStatusView.vue"),
	meta: {
		icon: 'mdi-alert',
		title: 'Quotation Status',
		group: _collectionGrp,
		order: 3
	}
})


const createRoleView = () => ({
	path: "user-role",
	name: "Role Settings",
	component : () => import("../../views/RoleSettingView.vue"),
	meta: {
		icon: "mdi-account-question",
		title: "Role Settings",
		permission : [PermissionType.USER],
		group: _settingsGrp,
		order: 2
	}
})

const createUsersListView = () => ({
	path: "users-settings",
	name: "Users Settings",
	component: () => import("../../views/UsersSettingsView.vue"),
	meta: {
		icon: 'mdi-account-box',
		title: 'Users Settings',
		permission : [PermissionType.USER],
		group: _settingsGrp,
		order: 3
	}
})


const treeItems = [
	createLoginView(),
	createPortalView([
		createCollectionGroup(),
		createSettingGroup(),
		createManagementGroup(),
	], [
		createDashboardView(),
		createRoleView(),
		createUsersListView(),
		createQuotationRequestView(),
		createQuotationStatusView(),
		createCollectionListView(),
	]),	
].filter(s => s != null);


resolveFullPath(treeItems);
export const routeList = getRouteList(treeItems)
export const routeTree = getRouteTree(treeItems)
