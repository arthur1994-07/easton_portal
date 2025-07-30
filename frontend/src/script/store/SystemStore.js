'use strict'

export default  {
	namespaced : true,
	state: () => ({
		userProfile: [],
	}),
	actions : {
		updateUserProfile: ({commit}, val) => commit('setUserProfile', val),
	},
	mutations : {
		setUserProfile: (state, val) => state.userProfile = val,
	}
}
