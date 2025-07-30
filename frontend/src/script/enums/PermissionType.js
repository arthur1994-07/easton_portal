'use strict'

export const USER = 'edit_user'
export const SYS_CONFIG = 'edit_system_config'
export const CREATE_DELETE_ORGANIZATION = 'create_delete_organization'
export const CREATE_DELETE_ORGANIZATION_FOR_SUPERUSER = 'create_delete_organization_superuser'
export const ONLINE_AMR_LICENSE = 'edit_online_amr_license'
export const OFFLINE_AMR_LICENSE = 'edit_offline_amr_license'
export const GENERATE_CERTIFICATE = 'generate_certificate'


export const permissionList = [
	{ key: USER, display: 'User Management'},
	{ key: SYS_CONFIG, display: 'System Config Administrator Management'},
	{ key: CREATE_DELETE_ORGANIZATION, display: 'Organization Management'},
	{ key: CREATE_DELETE_ORGANIZATION_FOR_SUPERUSER, display: 'Superuser Organization Management'},
	{ key: ONLINE_AMR_LICENSE, display: 'Online AMR License Management'},
	{ key: OFFLINE_AMR_LICENSE, display: 'Offline AMR License Management'},
	{ key: GENERATE_CERTIFICATE, display: 'Generate Certificate'},
]
