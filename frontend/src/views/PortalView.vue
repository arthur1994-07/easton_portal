<template>
	<q-layout view="lHh Lpr lFf">
		<q-header elevated class="glossy">	
			<div>
				<q-toolbar>
					<q-btn flat dense round aria-label="Menu" icon="mdi-menu"
						@click="sliderBar = !sliderBar"
					/>
					<q-toolbar-title class="row">
						<q-img :src="companyLogo" fit="contain" class="q-ma-sm" style="max-width: 120px" />
						<div class="q-ma-md">Easton Industrial Ltd</div>
					</q-toolbar-title>
				</q-toolbar>
			</div>			
		</q-header>
		<q-drawer v-model="sliderBar" :mini="mini" class="bg-accent">
			<q-item clickable @click="mini = !mini">
				<q-item-section avatar>
					<q-icon color="white" :name="mini ? 'mdi-chevron-double-right' : 'mdi-chevron-double-left'" />
				</q-item-section>
				<q-item-section>
					<q-item-label class="text-white">Collapse Sidebar</q-item-label>
				</q-item-section>
			</q-item>
			<q-separator dark />
			<q-list>
				<div v-for="(item, index) in items" :key="index">
					<div v-if="item.subItems.length != 0 && mini">
						<q-item clickable :active="activeKey == item.key" active-class="bg-positive" @click="changeGroupState(item)">
							<q-item-section v-if="item.icon != null" avatar>
								<q-icon color="white" :name="item.icon" />
							</q-item-section>
							<q-item-section>
								<q-item-label class="text-white">{{ item.displayText }}</q-item-label>
							</q-item-section>
						</q-item>
						<div v-if="groupsState[item.key]">
							<q-item v-for="sub in item.subItems" :key="sub.key" clickable :active="activeKey == sub.key" active-class="bg-primary"
								@click="navigateTo(sub)"
							>
								<q-item-section v-if="sub.icon != null" avatar class="text-white">
									<q-icon color="white" :name="sub.icon" />
								</q-item-section>
								<q-item-section>
									<q-item-label class="text-white">{{ sub.displayText }}</q-item-label>
								</q-item-section>
							</q-item>
						</div>
					</div>
					<q-expansion-item v-else-if="item.subItems.length != 0" v-model="groupsState[item.key]" :icon="item.icon" :label="item.displayText" 
						header-class="text-white" active-class="bg-info" @update:model-value="navigateTo(item)"
					>
						<q-item v-for="sub in item.subItems" :key="sub.key" clickable :active="activeKey == sub.key" active-class="bg-primary"
							@click="navigateTo(sub)"
						>
							<q-item-section v-if="sub.icon != null" avatar class="text-white">
								<q-icon color="white" :name="sub.icon" />
							</q-item-section>
							<q-item-section>
								<q-item-label class="text-white">{{ sub.displayText }}</q-item-label>
							</q-item-section>
						</q-item>
					</q-expansion-item>
					<q-item v-else clickable :active="activeKey == item.key" active-class="bg-info" @click="navigateTo(item)">
						<q-item-section v-if="item.icon != null" avatar>
							<q-icon color="white" :name="item.icon" />
						</q-item-section>
						<q-item-section>
							<q-item-label class="text-white">{{ item.displayText }}</q-item-label>
						</q-item-section>
					</q-item>
				</div>
			</q-list>
			<q-separator dark />
			<q-item clickable @click="signOut">
				<q-item-section avatar>
					<q-icon color="white" name="mdi-logout" />
				</q-item-section>
				<q-item-section>
					<q-item-label class="text-white">Log Out</q-item-label>
				</q-item-section>
			</q-item>
		</q-drawer>
		<q-page-container>
			<router-view />
		</q-page-container>
	</q-layout>
</template>

<script>
'use strict'

import { defineComponent, ref, onMounted } from "vue";
import { constructRouterTree, constructGroupItemState } from "../script/utils/createRouteTree";
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import router from '../script/plugins/vuerouter'
import * as SessionStorage from "../script/utils/session_storage.js";
import * as PopupDialog from '../script/utils/PopupDialog.js'
import auth from '../script/services/AuthService.js'
import currentUserService from "../script/services/CurrentUserService.js";

export default defineComponent({
	setup() {
		const route = useRoute()
		const store = useStore()
		const items = ref([])
		const groupsState = ref([])
		const activeKey = ref("")
		const sliderBar = ref(true)
		const mini = ref(true)
		const companyLogo = ref(new URL('../assets/logo.png', import.meta.url).href)

		onMounted(() => {
			const path = SessionStorage.retrieveValue(SessionStorage.targetPath)
			if (path == null) return
			router.push(path)
		})

		onMounted(async () => {
			try {
				const permissions = await currentUserService.permission()
				store.dispatch('ui/setPermission', permissions)
				const current = await currentUserService.current();
				console.log(current)
				store.dispatch('system/updateUserProfile', current)

				items.value = constructRouterTree(route, permissions)

				groupsState.value = constructGroupItemState(items.value)
				PopupDialog.show(store, PopupDialog.SUCCESS, "Login successful")
			} catch (err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		})

		const signOut = async () => {
			const loginUrl = "/login"
			
			if (store.state.ui.idToken != null) {
				try {
					var url = await currentUserService.logoutUrl()
					if (url != null) {
						auth.oAuth.signOut(url, store.state.ui.idToken, loginUrl)
						return;
					}
				} catch(err) {
					PopupDialog.show(store, PopupDialog.FAILURE, err.message)
				} 
			}
			auth.signOut()
			router.push(loginUrl)
		}

		const changeGroupState = (item) => {
			groupsState.value[item.key] = !groupsState.value[item.key];
			if (groupsState.value[item.key]) activeKey.value = item.key;
		}

		const navigateTo = (item) => {
			activeKey.value = item.key;
			if (item.path == undefined || route.fullPath == item.path) return
			router.push(item.path)
				.catch(err => {
					if (err.name != "NavigationDuplicated") throw err
				})
		}

		return { items, mini, sliderBar, activeKey, groupsState, companyLogo,changeGroupState, navigateTo, signOut }
	}
})


</script>

