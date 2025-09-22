<template>
	<two-button-dialog ref="removeDialog" title="Delete Quotation" successText="Confirm"
		cancelText="Cancel" :full-width="$q.screen.lt.md? true: false"
	>
		<template v-slot="{data}">
			<q-card-section>
				<div class="text-primary">Quotation {{ data.name }} will be deleted, please confirm.</div>
			</q-card-section>
		</template>
	</two-button-dialog>
	<two-button-dialog ref="displayDialog" title="Collection Display" successText="Exit" cancelText="Close" :full-width="$q.screen.lt.md? true: false">
		<template v-slot="{data}">
			<q-card-section>
				<image-collection-display-component :images="data" />
			</q-card-section>
		</template>
	</two-button-dialog>
	<two-button-dialog ref="createDialog" :title="createMode ? 'Create Collection' : 'Edit Collection'" :full-width="$q.screen.lt.md? true: false">
		<template v-slot="{data}">
			<q-card-section>
				<div class="items-center q-my-sm">
					<q-input v-model="data.name" bg-color="secondary" :dark="true" filled
						label-color="white" label="Name"
						:rules="[
							val => !!val || 'Field is required',
						]"
					/>
					<q-select 
						v-model="data.year"
						filled 
						:dark="true"
						bg-color="secondary"
						text-color="primary"
						label-color="primary"
						:options="listOfYears"
					>
						<template v-slot:hint>
							Field hint
						</template>

						<template v-slot:append>
							<q-icon name="event" />
						</template>
					</q-select>
					<q-toggle 
						v-model="data.isProtected" 
						class="text-primary"
						:label="data.isProtected ? 'Protected' : 'Unprotected'"
						color="dark" 
						keep-color
						icon="mdi-checkbox-blank-circle" 
					/>
				</div>
				<div>
					<q-btn class="row items-start" color="info" @click="importCollectionPictures(!createMode)">
						<q-icon name="mdi-import" color="white" />
						<q-tooltip>import file</q-tooltip>
					</q-btn>
					<q-item class="text-primary items-center">uploaded file : {{ collections?.name }}</q-item>
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
				<template v-slot:body-cell-name="{ value, row }">	
					<q-td>
						<div class="row items-center justify-start no-wrap">
							<div class="q-ma-sm">{{ value }}</div>
							<q-chip v-if="row.isProtected" color="red" size="sm" outline>
								New Collection
							</q-chip>
						</div>
					</q-td>
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
							<q-item clickable class="q-py-none q-px-xs" style="min-height : 0;" @click="displayAction(row)">
								<q-icon size="sm" color="white" name="mdi-image-album" />
								<q-tooltip>view collection</q-tooltip>
							</q-item>
						</div>
					</q-td>
				</template>
			</q-table>
		</q-card>
	</q-page>
</template>

<script>
import { ref, defineComponent, onMounted, readonly, watch } from "vue";
import { useStore } from 'vuex';
import { dataUrlToBase64String, fileToDataUrl, showFileDialog } from '../script/utils/utils.js'
import * as PopupDialog from '../script/utils/PopupDialog.js'
import TwoButtonDialog from "../components/TwoButtonDialog.vue";
import CurrentUserService from "../script/services/CurrentUserService.js";
import CollectionService from "../script/services/CollectionService.js";
import WebService from "../script/services/WebService.js";
import RequestService from "../script/services/RequestService.js";
import ImageCollectionDisplayComponent from "../components/ImageCollectionDisplayComponent.vue";




export default defineComponent ({
	components: {
		TwoButtonDialog,
		ImageCollectionDisplayComponent
	},
	setup() {
		const store = useStore()
		const items = ref([])
		const createMode = ref(false)
		const requestDialog = ref(null)
		const createDialog = ref(null)
		const displayDialog = ref(null)
		const path = ref(null)
		const currentUser = ref(null)
		const collections = ref([])
		const base64Profiles = ref([])
		const listOfYears = ref([])
		const model = ref(null)
		const isProtected = ref(false)
		const imageCollections = ref([])

		const columns = readonly([
			{ name: 'name', required: true, label: 'name', align: 'left', field: 'name', sortable: true },
			{ name: 'actions', label: 'actions', align: 'center', field: 'actions' }
		])

		const displayAction = async (item) => {	
			if (item == null) return
			imageCollections.value = await CollectionService.findImageById({ id: item.id }, path.value.url)
			console.log(imageCollections.value)
			let data = await displayDialog.value.run({
				images : imageCollections.value
			})
			if (data == null) return
		}

		const removeDialog = ref(null)
		const removeAction = async (item) => {
			let data = await removeDialog.value.run({ name: item.name });
			if (data == null) return

			try {
				const accessToken = window.sessionStorage.getItem("accessToken")
				await CollectionService.remove({ id: item.id }, accessToken, path.value.url);
				items.value = await CollectionService.list(accessToken, path.value.url)

				PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully removed collection");
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		const editAction = async (item) => {
			createMode.value = item == null
			base64Profiles.value = null
			let data = await createDialog.value.run({
				id: item?.id,
				name: item?.name,
				year: item?.year,
				isProtected: isProtected.value,
			}, {
				successCheck : (data) => data.name != "" && data.year != 0 
			});
			if (data == null) return;

			try {
				if (createMode.value) {
					await CollectionService.create(
						{
							name: data.name,
							year: data.year,
							isProtected: data.isProtected,
							image: base64Profiles.value
						}
						, window.sessionStorage.getItem("accessToken"), path.value.url)
					PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully created collection")
				} else {
					if (base64Profiles.value.length != 0) {	
						let requestList = []
						requestList = (base64Profiles.value.map(s => ({
							id: item.id,
							image: s,
							format: "pgm"
						})))
						await Promise.all(requestList.map(s => 
							CollectionService.update(s,window.sessionStorage.getItem("accessToken"), path.value.url)))
					}
					PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully edited collection")
				}
				items.value = await CollectionService.list(window.sessionStorage.getItem("accessToken"), path.value.url)
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		


		const importCollectionPictures = async (isMultiple) => {
			collections.value = null
			collections.value = await showFileDialog(".png", isMultiple)
			if (collections.value == null || collections.value.length <= 0) return

			var urls = isMultiple ? [] : null 

			urls = isMultiple ? await Promise.all(collections.value.map(async s => await fileToDataUrl(s))) :
				await fileToDataUrl(collections.value)

			if (urls == null || urls.length <= 0) return

			base64Profiles.value = isMultiple ? urls.map(s => dataUrlToBase64String(s)) : dataUrlToBase64String(urls);
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

		const getListOfYears = () => {
			let currentYear = new Date().getFullYear()
			const earliestYear = 2010;

			while (currentYear >= earliestYear) {
				listOfYears.value.push(currentYear)
				currentYear -= 1;
			}
		}
				
		watch(isProtected, () => console.log(isProtected.value))

		onMounted(async () => {
			try{
				getListOfYears()
				currentUser.value = await CurrentUserService.current()
				path.value = await WebService.getInfo()
				const accessToken = await CollectionService.webAuthenticate(currentUser.value.uuid, currentUser.value.username, path.value.url)
				window.sessionStorage.setItem("accessToken", accessToken)

				items.value = await CollectionService.list(accessToken, path.value.url)
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		})

		return { model, isProtected, items, imageCollections, columns, collections, listOfYears, createMode, displayDialog, createDialog, removeDialog, requestDialog, 
			importCollectionPictures, removeAction, editAction, displayAction, requestAction }
	}
})
</script>