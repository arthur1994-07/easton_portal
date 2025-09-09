<template>
	<two-button-dialog ref="removeDialog" title="Delete Quotation" successText="Confirm"
		cancelText="Cancel"
	>
		<template v-slot="{data}">
			<q-card-section>
				<div class="text-primary">Quotation {{ data.name }} will be deleted, please confirm.</div>
			</q-card-section>
		</template>
	</two-button-dialog>
	<two-button-dialog ref="createDialog" :title="createMode ? 'Create Collection' : 'Edit Collection'" cardStyle="max-width: 50vw; width : 50vw">
		<template v-slot="{data}">
			<q-card-section>
				<div class="items-center q-my-sm">
					<q-input v-model="data.name" bg-color="grey-3" :dark="false" filled
						label-color="primary" label="name"
						:rules="[
							val => !!val || 'Field is required',
						]"
					/>
				</div>
				<div class="items-center q-my-sm">
					<q-input v-model="data.key" input-class="text-black" filled bg-color="grey-3"
						label-color="primary" label="key" 
						:rules="[
							val => !!val || 'Field is required',
							val => /^[a-zA-Z0-9-_]*$/.test(val) || 'Invalid format'
						]"
					/>
				</div>
			</q-card-section>
		</template>
	</two-button-dialog>
	<two-button-dialog ref="requestDialog" title="Quotation request" cardStyle="max-width: 50vw; width : 50vw">
		<template v-slot="{data}">
			<q-card-section>
				<div class="items-center q-my-sm">
					<q-input v-model="data.remarks" bg-color="grey-3" :dark="false" filled
						label-color="primary" label="remarks"
						:rules="[
							val => !!val || 'Field is required',
						]"
					/>
				</div>
			</q-card-section>
		</template>
	</two-button-dialog>
	<q-page class="q-pa-sm">
		<q-card class="bg-primary">
			<q-card-section class="row justify-between">
				<div class="text-section-color text-h6">Collection List</div>
				<div>
					<q-btn push class="q-mx-xs" color="white" @click="editAction(null)">
						<q-icon color="primary" name="mdi-plus" />
						<q-tooltip>Collection</q-tooltip>
					</q-btn>
				</div>
			</q-card-section>
			<q-separator dark />
			<q-table class="bg-primary text-white q-mx-md" :pagination="{rowsPerPage: 7}" :columns="columns" row-key="name" :rows="items">
				<template v-slot:no-data="{icon}">
					<div>
						<q-icon size="sm" :name="icon" />
						no data
					</div>
				</template>
				<template v-slot:body-cell-actions="{ row }">
					<q-td>
						<div class="row justify-center">
							<q-item clickable class="q-py-none q-px-xs" style="min-height : 0;" @click="editAction(row)">
								<q-icon size="sm" color="white" name="mdi-pencil" />
								<q-tooltip>edit item</q-tooltip>
							</q-item>
							<q-item clickable class="q-py-none q-px-xs" style="min-height : 0;" @click="removeAction(row)">
								<q-icon size="sm" color="white" name="mdi-delete" />
								<q-tooltip>delete item</q-tooltip>
							</q-item>
							<q-item clickable class="q-py-none q-px-xs" style="min-height : 0;" @click="requestAction(row)">
								<q-icon size="sm" color="white" name="mdi-file-document" />
								<q-tooltip>quotation request</q-tooltip>
							</q-item>
						</div>
					</q-td>
				</template>
			</q-table>
		</q-card>
	</q-page>
</template>

<script>
import { ref, defineComponent, onMounted, readonly } from "vue";
import { useStore } from 'vuex';
import * as PopupDialog from '../script/utils/PopupDialog.js'
import TwoButtonDialog from "../components/TwoButtonDialog.vue";
import CurrentUserService from "../script/services/CurrentUserService.js";
import CollectionService from "../script/services/CollectionService.js";
import WebService from "../script/services/WebService.js";
import RequestService from "../script/services/RequestService.js";
// import SelectionDialog from "../components/SelectionDialog.vue";

// table with 2 columns

// request quotation button --> popup dialog to write info (text)
// view collection button
// remove quotation button (admin)
// view quotation status (admin)


export default defineComponent ({
	components: {
		TwoButtonDialog,
	},
	setup() {
		const store = useStore()
		const items = ref([])
		const createDialog = ref(null)
		const createMode = ref(false)
		const requestDialog = ref(null)
		const path = ref(null)
		const currentUser = ref(null)

		const columns = readonly([
			{ name: 'name', required: true, label: 'name', align: 'left', field: 'name', sortable: true },
			{ name: 'actions', label: 'actions', align: 'center', field: 'actions' }
		])

		const removeDialog = ref(null)
		const removeAction = async (item) => {
			let data = await removeDialog.value.run({ name: item.name });
			if (data == null) return

			try {
				// await oAuthService.remove({ id: item.id });
				// items.value = await oAuthService.list();
				PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully removed collection");
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		const editAction = async (item) => {
			createMode.value = item == null
			let data = await createDialog.value.run({
				name: item?.name,
				key: item?.key
			}, {
				successCheck : (data) => data.name != ""
			});
			if (data == null) return;

			try {
				if (createMode.value) {
					// await organizationService.create({
					// 	name: data.name,
					// 	key: data.key
					// })
					PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully created organization")
				} else {
					// await organizationService.update({
					// 	id: item.id,
					// 	name: data.name,
					// 	key: data.key
					// })
					PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully edited organization")
				}
				items.value = await CollectionService.requestCollectionList(currentUser.value.uuid, currentUser.value.username, path.value)
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}
		
		const requestAction = async (item) => {
			if (item == null) return
			let data = await requestDialog.value.run({
				remarks: item?.remarks
			}, {
				successCheck : (data) => data.remarks != ""
			});
			if (data == null) return;
			try {
				// request service create quotation request 
				await RequestService.create({
					customerId: currentUser.value.uuid,
					customerName: currentUser.value.username,
					customerEmail: currentUser.value.email,
					collectionName: item.name,
					remarks: data.remarks
				})
				PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully requested new quotation")

			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}
		


		onMounted(async () => {
			try{
				currentUser.value = await CurrentUserService.current()
				path.value = await WebService.getInfo()
				items.value = await CollectionService.requestCollectionList(currentUser.value.uuid, currentUser.value.username, path.value.url)
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		})

		return { items, columns, createMode, createDialog, removeDialog, requestDialog, removeAction, editAction, requestAction }
	}
})
</script>