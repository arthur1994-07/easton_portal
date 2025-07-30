'use strict'

import { createRouter, createWebHistory } from 'vue-router'
import { routeTree } from '../utils/route_tree.js'
import auth from '../services/AuthService.js'
import { config } from '../../config/config.js'

const router = createRouter({
	base: config.$prefix_base,
	history: createWebHistory(),
	routes : routeTree,
})

router.beforeEach((to, from, next) => {
	if (!to.meta.bypassAuth && !auth.authenticated())  {
		auth.signOut()
		next({path: '/login'})
		return
	}
	next() // make sure to always call next()!
})

export default router;