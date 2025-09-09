<template>
	<two-button-dialog ref="removeDialog" title="Remove Confirmation" successText="Confirm"
		cancelText="Cancel"
	>
		<template v-slot="{data}">
			<q-card-section>
				<div class="text-primary"> Role {{ data.name }} will be deleted, please confirm.</div>
			</q-card-section>
		</template>
	</two-button-dialog>
	<two-button-dialog ref="editDialog" :title="editTitle" successText="Save"
		cancelText="Cancel"
	>
		<template v-slot="{data}">
			<q-card-section>
				<q-input v-model="data.name" bg-color="grey-3" label-color="primary" :dark="false" filled 
					label="Role Name"
					:rules="[val => !!val || 'Field is required']"
				/>
			</q-card-section>
		</template>
	</two-button-dialog>
	<q-page class="q-pa-sm">
		<q-card class="bg-primary">
			<q-card-section class="row justify-between">
				<div class="text-section-color text-h6">Role Management</div>
				<div>
					<q-btn v-if="selectedRole && editMode" push class="q-mx-xs" color="white" @click="changeRightEditMode(false)">
						<q-icon color="primary" name="mdi-close" />
						<q-tooltip>cancel edit</q-tooltip>
					</q-btn>
					<q-btn v-if="selectedRole && editMode" push class="q-mx-xs" color="white" @click="saveRightChange()">
						<q-icon color="primary" name="mdi-content-save" />
						<q-tooltip>save</q-tooltip>
					</q-btn>
					<q-btn v-if="selectedRole && !editMode" push class="q-mx-xs" color="white" @click="changeRightEditMode(true)">
						<q-icon color="primary" name="mdi-pencil" />
						<q-tooltip>Edit Right(s) for Selected Role</q-tooltip>
					</q-btn>
					<q-btn v-if="!editMode" push class="q-mx-xs" color="white" @click="editAction(null)">
						<q-icon color="primary" name="mdi-plus" />
						<q-tooltip>New Role</q-tooltip>
					</q-btn>
				</div>
			</q-card-section>
			<q-separator dark />
			<div class="row q-ma-sm">
				<div class="col-md-4 col-12" style="min-height:70vh">
					<q-scroll-area class="fit">
						<q-list class="q-ma-xs">
							<q-card v-for="(item) in roles" :key="item.id">
								<q-item class="q-mx-xs q-my-sm row" clickable 
									:active="selectedRole?.id == item.id" active-class="bg-green-8" @click="changeSelectedItem(item)"
								>
									<q-item-section avatar>
										<q-icon size="sm" name="mdi-account-group" color="white" />
									</q-item-section>
									<q-item-section class="text-white text-left">{{ item.name }}</q-item-section>
									<q-item-section side>
										<q-btn v-if="selectedRole?.id == item.id" flat dense round icon="mdi-dots-vertical">
											<q-menu fit class="text-no-wrap bg-accent">
												<q-list>
													<q-item v-close-popup clickable @click="editAction(item)">
														<q-item-section avatar>
															<q-icon color="white" name="mdi-rename-box" />
														</q-item-section>
														<q-item-section>rename role</q-item-section>
													</q-item>
													<q-item v-close-popup clickable @click="removeAction(item)">
														<q-item-section avatar>
															<q-icon color="white" name="mdi-delete" />
														</q-item-section>
														<q-item-section>delete role</q-item-section>
													</q-item>
												</q-list>
											</q-menu>
										</q-btn>
									</q-item-section>
								</q-item>
								<role-info-component v-if="selectedRole?.id == item.id && $q.screen.lt.md" 
									class="q-ma-xs q-pa-sm bg-accent" 
									:role="selectedRole" :rights="rights" :editMode="editMode" :editingSet="editingRights"
									@update:editingSet="(key, value) => editingRights[key] = value"
								/>
							</q-card>
						</q-list>
					</q-scroll-area>
				</div>
				<div v-if="$q.screen.gt.sm" class="row col-grow">
					<q-separator dark vertical />
					<q-scroll-area class="col-grow q-ma-sm">
						<role-info-component v-if="selectedRole" 
							:role="selectedRole" :rights="rights" :editMode="editMode" :editingSet="editingRights"
							@update:editingSet="(key, value) => editingRights[key] = value"
						/>	
					</q-scroll-area>
				</div>
			</div>
		</q-card>
	</q-page>
</template>

<script>
import { defineComponent, ref, onMounted, watch } from "vue";
import { useStore } from 'vuex'
import TwoButtonDialog from '../components/TwoButtonDialog.vue'
import RoleInfoComponent from "../components/RoleInfoComponent.vue";
import RoleService from "../script/services/RoleService.js";
import * as PopupDialog from '../script/utils/PopupDialog.js'

export default defineComponent ({
	components: {
		TwoButtonDialog,
		RoleInfoComponent
	},
	setup() {
		const store = useStore()
		const roles = ref([])
		const selectedRole = ref(null)
		const editMode = ref(false)
		const rights = ref([])
		const editingRights = ref(null)

		onMounted(async () => {
			try {
				roles.value = await RoleService.list()
				selectedRole.value = null
				editMode.value = false
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
			
		})

		onMounted(async () => {
			try {
				rights.value = await RoleService.rightList()
				editingRights.value = rights.value.reduce((acc, current) => (acc[current.id] = false, acc), {})
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		})

		const changeSelectedItem = (item) => {
			selectedRole.value = item
			editMode.value = false
		}

		const editDialog = ref(null)
		const editTitle = ref(null)

		const editAction = async (item) => {
			editTitle.value = item == null ? "Create Role" : "Edit Role"
			let data = await editDialog.value.run(
				{ 
					name: item?.name ?? `New Role ${roles.value.length + 1}`
				},{
					successCheck : (data) => data.name != ""
				});
			if (data == null) return

			const action = item == null ? () => RoleService.add({name : data.name}) :
				() => RoleService.rename({ id: item.id, name : data.name });

			try {
				await action()
				roles.value = await RoleService.list();
				selectedRole.value = roles.value.find(s => s.id == selectedRole.value?.id)
				PopupDialog.show(store, PopupDialog.SUCCESS, item == null ? "Successfully added new role" : "Successfully updated role")
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}
		
		const removeDialog = ref(null)
		const removeAction = async (item) => {
			let data = await removeDialog.value.run({ name: item.name });
			if (data == null) return

			try {
				await RoleService.remove({ id : item.id });
				roles.value = await RoleService.list();
				selectedRole.value = null
				PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully removed role");
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		const changeRightEditMode = (mode) => {
			editMode.value = mode;
			if (mode && selectedRole.value != null) {
				selectedRole.value.rights.forEach(s => editingRights.value[s] = true)
			}
		}

		const saveRightChange = async () => {
			try {	
				var data = rights.value.filter(s => editingRights.value[s.id]).map(s => s.id)
				await RoleService.updateRights({ id : selectedRole.value.id, rights: data});
	
				roles.value = await RoleService.list();
				selectedRole.value = roles.value.find(s => s.id == selectedRole.value?.id)
				editMode.value = false
				PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully update right for selected role");
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		watch(selectedRole, () => {
			editingRights.value = rights.value.reduce((acc, current) => (acc[current.id] = false, acc), {})
			selectedRole.value?.rights.forEach(s => editingRights.value[s] = true)
		})

		return { roles, editMode, editDialog, removeDialog, selectedRole, editTitle, editingRights, rights, 
			saveRightChange, changeRightEditMode, editAction, removeAction, changeSelectedItem }
	}
})
</script>
