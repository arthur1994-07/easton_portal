'use strict'

import { resolveFullPath, getRouteTree, getRouteList } from '../core/RouteTree.js'
import * as PermissionType from '../enums/PermissionType.js'

// group property
const _settingsGrp = 'settings'
const _organizationGrp = 'organization'

const createOrganizationGroup = () => ({
	icon: "mdi-account-supervisor",
	title: "Organization",
	key: _organizationGrp,
	order: 2
})

const createSettingGroup = () => ({
	icon: "mdi-cog",
	title: "Settings",
	key: _settingsGrp,
	order: 1
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

const createUserProfileView = () => ({
	path: "user-profile",
	name: "User Profile",
	component : () => import("../../views/UserProfileView.vue"),
	meta: {
		icon: 'mdi-human-greeting',
		title: 'User Profile',
		group: _settingsGrp,
		order: 1
	}
})

const createOrganizationView = () => ({
	path: "organization",
	name: "Organization Management",
	component: () => import("../../views/EditOrganizationView.vue"),
	meta: {
		icon: 'mdi-account-group',
		title: 'Organization Management',
		permission : [PermissionType.CREATE_DELETE_ORGANIZATION, PermissionType.CREATE_DELETE_ORGANIZATION_FOR_SUPERUSER],
		group: _organizationGrp,
		order: 1
	}
})

const createRoleView = () => ({
	path: "user-role",
	name: "Role Settings",
	component : () => import("../../views/EditRoleView.vue"),
	meta: {
		icon: "mdi-account-question",
		title: "Role Settings",
		permission : [PermissionType.USER],
		group: _organizationGrp,
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
		group: _organizationGrp,
		order: 3
	}
})


const treeItems = [
	createLoginView(),
	createPortalView([
		createOrganizationGroup(),
		createSettingGroup(),
	], [
		createOrganizationView(),
		createRoleView(),
		createUsersListView(),

		createUserProfileView(),
	]),	
].filter(s => s != null);


resolveFullPath(treeItems);
export const routeList = getRouteList(treeItems)
export const routeTree = getRouteTree(treeItems)
