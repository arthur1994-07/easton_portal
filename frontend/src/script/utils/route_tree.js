'use strict'

import { resolveFullPath, getRouteTree, getRouteList } from '../core/RouteTree.js'
import * as PermissionType from '../enums/PermissionType.js'

// group property
const _settingsGrp = 'settings'
// const _organizationGrp = 'organization'
// const _userGrp = 'user'
const _collectionGrp = 'collection'

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



// const createOrganizationGroup = () => ({
// 	icon: "mdi-account-supervisor",
// 	title: "Organization",
// 	key: _organizationGrp,
// 	order: 2
// })

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
		title: "Dashboard",
		order: 0,
	}
})

// const createUserProfileView = () => ({
// 	path: "user-profile",
// 	name: "User Profile",
// 	component : () => import("../../views/UserProfileView.vue"),
// 	meta: {
// 		icon: 'mdi-human-greeting',
// 		title: 'User Profile',
// 		group: _settingsGrp,
// 		order: 1
// 	}
// })

const createCollectionListView = () => ({
	path: "collection-list",
	name: "Collection list",
	component : () => import("../../views/EditCollectionListView.vue"),
	meta: {
		icon: 'mdi-gift',
		title: 'Collection Action',
		permission: [PermissionType.COLLECTION],
		group: _collectionGrp,
		order: 1
	}
})

const createCollectionActionView = () => ({
	path: "collection-action",
	name: "Collection Action",
	component : () => import("../../views/EditCollectionActionView.vue"),
	meta: {
		icon: 'mdi-email-alert',
		title: 'Collection Action',
		permission: [PermissionType.COLLECTION],
		group: _collectionGrp,
		order: 2
	}
})

// const createOrganizationView = () => ({
// 	path: "organization",
// 	name: "Organization Management",
// 	component: () => import("../../views/EditOrganizationView.vue"),
// 	meta: {
// 		icon: 'mdi-account-group',
// 		title: 'Organization Management',
// 		permission : [PermissionType.CREATE_DELETE_ORGANIZATION, PermissionType.CREATE_DELETE_ORGANIZATION_FOR_SUPERUSER],
// 		group: _organizationGrp,
// 		order: 1
// 	}
// })

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
		icon: 'mdi-account-multiple',
		title: 'Users Settings',
		permission : [PermissionType.USER],
		group: _settingsGrp,
		order: 3
	}
})


const treeItems = [
	createLoginView(),
	createPortalView([
		// createOrganizationGroup(),
		createCollectionGroup(),
		createSettingGroup(),
	], [
		createDashboardView(),
		// createOrganizationView(),
		createRoleView(),
		createUsersListView(),
		createCollectionActionView(),
		createCollectionListView(),

		// createUserProfileView(),
	]),	
].filter(s => s != null);


resolveFullPath(treeItems);
export const routeList = getRouteList(treeItems)
export const routeTree = getRouteTree(treeItems)
