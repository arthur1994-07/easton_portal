'use strict'

export default {
	namespaced : true,
	state: () => ({
		messagePacket: null,
		permission: null,
		autoLogin: true,
		idToken: null,
	}),
	actions : {
		notify : ({commit}, val) => commit('notified', val),
		setPermission: ({commit}, val) => commit('modifyPermission', val),
		initialiseStore: ({commit}, val) => commit('processInitialiseStore', val),
		setIdToken :  ({commit}, val) => commit('modifyIdToken', val),
		setAutoLogin : ({commit}, val) => commit('modifyAutoLogin', val),
	},
	mutations : {
		notified: (state, val) => state.messagePacket = val,
		modifyPermission: (state, val) => state.permission = val,
		modifyAutoLogin: (state, val) => state.autoLogin = val,
		modifyIdToken : (state, val) => state.idToken = val,
		processInitialiseStore: () => {
		}
	}
}
