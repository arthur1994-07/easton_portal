<template>
	<two-button-dialog ref="removeDialog" title="Remove User" successText="Confirm"
		cancelText="Cancel"
	>
		<template v-slot="{data}">
			<q-card-section>
				<div class="text-primary">User {{ data.name }} will be deleted, please confirm.</div>
			</q-card-section>
		</template>
	</two-button-dialog>
	<selection-dialog ref="roleDialog" :selectMultiple="true" 
		title="Roles" successText="Select" cancelText="Cancel"
	>
		<template v-slot="{data}">
			<q-item-section avatar>
				<q-avatar color="white" size="md" text-color="primary">
					<q-icon color="primary" name="mdi-account-group" />
				</q-avatar>
			</q-item-section>
			<q-item-section>
				<div class="text-white">{{ data.name }}</div>
			</q-item-section>
		</template>
		<template v-slot:no-data>no data</template>
	</selection-dialog>
	<q-page class="q-pa-sm">
		<q-card class="bg-primary">
			<q-card-section class="row">
				<div class="text-section-color text-h6">User Management</div>
			</q-card-section>
			<q-separator dark />
			<!-- <div class="row items-center">
				<q-select v-model="selectedDomain" filled bg-color="secondary" text-color="primary" label-color="white"
					class="q-pa-sm col"
					:label="t('user_view_select_domain_name')" 
					:options="domains"
				/>
			</div> -->
			<q-separator dark />
			<div class="row no-wrap q-ma-sm">
				<div class="col-md-4 col-12 column items-stretch q-pa-sm" style="min-height:70vh">
					<q-list class="q-ma-xs col-grow">
						<q-card v-for="(item) in users" :key="item.id">
							<q-item class="q-mx-xs q-my-sm row" clickable 
								:active="selectedUser?.id == item.id" active-class="bg-green-8" @click="changeSelectedItem(item)"
							>
								<q-item-section avatar>
									<q-icon v-if="!item.disable" size="sm" name="mdi-account" color="blue" />
									<q-icon v-else size="sm" name="mdi-account" color="red" />
								</q-item-section>
								<q-item-section class="text-white text-left">{{ item.accountName ?? item.email }}</q-item-section>
													
								<q-item-section side>
									<q-btn v-if="selectedUser?.id == item.id" flat dense round icon="mdi-dots-vertical">
										<q-menu fit class="text-no-wrap bg-accent">
											<q-list>
												<q-item v-close-popup clickable @click="editRoleAction(item)">
													<q-item-section avatar>
														<q-icon color="white" name="mdi-pencil" />
													</q-item-section>
													<q-item-section>Edit Role</q-item-section>
												</q-item>
												<!-- implement remove function later -->
												<!-- <q-item v-close-popup clickable @click="removeAction(item)">
													<q-item-section avatar>
														<q-icon color="white" name="mdi-delete" />
													</q-item-section>
													<q-item-section>Delete User</q-item-section>
												</q-item> -->
											</q-list>
										</q-menu>
									</q-btn>
								</q-item-section>
							</q-item>
							<user-info-component v-if="selectedUser?.id == item.id && $q.screen.lt.md" :user="selectedUser"
								class="q-ma-xs q-pa-sm bg-accent" 
							/>
						</q-card>
					</q-list>
					<q-pagination
						v-model="currentPage"
						class="self-center"
						:max="totalPages"
						input
						input-class="text-white"
						color="white"
					/>
				</div>
				<div v-if="$q.screen.gt.sm" class="row col-grow">
					<q-separator dark vertical />
					<q-scroll-area class="col-grow q-ma-sm">
						<user-info-component v-if="selectedUser" :user="selectedUser" />	
					</q-scroll-area>
				</div>
			</div>
		</q-card>
	</q-page>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from "vue";
import { useStore } from 'vuex'

import TwoButtonDialog from '../components/TwoButtonDialog.vue'
import SelectionDialog from '../components/SelectionDialog.vue'
import UserInfoComponent from '../components/UserInfoComponent.vue'
import RoleService from "../script/services/RoleService.js";
import UserService from '../script/services/UserService.js'
import * as PopupDialog from '../script/utils/PopupDialog.js'



export default defineComponent ({
	components: {
		TwoButtonDialog,
		SelectionDialog,
		UserInfoComponent
	},
	setup() {
		const store = useStore()

		const selectedUser = ref(null)
		const currentPage = ref(1)
		const totalPages = ref(0)
		const unsortedUsers = ref([])
		const domainId = ref(2)
		const users =  computed(() => [...unsortedUsers.value].sort((a, b) => {
			return a.accountName ?? a.email < b.accountName ?? b.email ? 
				-1 : (a.accountName ?? a.email > b.accountName ?? b.email ? 1 : 0) 
		}))


		onMounted(async () => {
			try {
				unsortedUsers.value = await UserService.list({ domainId : domainId.value })
				selectedUser.value = null
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}) 

		const changeSelectedItem = (item) => selectedUser.value = item

		const removeDialog = ref(null)
		const removeAction = async (item) => {
			let data = await removeDialog.value.run({ name: item.accountName ?? item.email });
			if (data == null) return

			try {
				await UserService.remove({ id : item.id });
				unsortedUsers.value = await UserService.list({ domainId : domainId.value })
				selectedUser.value = null
				PopupDialog.show(store, PopupDialog.SUCCESS, 'Successfully removed user');
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		const roleDialog = ref(null)
		const roles = ref([])
		onMounted(async () => {
			try {
				roles.value = await RoleService.list()
				roles.value.splice(0, 0, { id: -1, name: "Administrator" })
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		})

		const editRoleAction = async (item) => {
			const selectedId = item.roles.map(s => s.id);
			if (item.administrator) selectedId.splice(0, 0, -1)
			
			const result = await roleDialog.value.run({ items: roles.value }, 
				{
					supportNoSelection : true,
					selectedIndices : selectedId.map(s => roles.value.findIndex(t => t.id == s)).filter(s => s >= 0)
				})

			if (result == null) return

			try {
				await UserService.changeRole({
					id : item.id, 
					administrator: result.selection.find(s => s.id == -1) != null,
					roles: result.selection.map(s => s.id).filter(s => s >= 0)
				});
				unsortedUsers.value = await UserService.list({ domainId : domainId.value })
				selectedUser.value = users.value.find(s => s.id == selectedUser.value?.id)
				PopupDialog.show(store, PopupDialog.SUCCESS, 'Successfully changed user role');
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}


		return { currentPage, totalPages, users, selectedUser, removeDialog, roleDialog, 
			changeSelectedItem, removeAction, editRoleAction }
	}
})
</script>