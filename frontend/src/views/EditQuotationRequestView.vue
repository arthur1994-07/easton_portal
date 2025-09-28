<template>
	<two-button-dialog ref="removeDialog" title="Remove Confirmation" successText="Confirm"
		cancelText="Cancel"
	>
		<template v-slot="{data}">
			<q-card-section>
				<div class="text-primary"> Quotation {{ data.name }} will be deleted, please confirm.</div>
			</q-card-section>
		</template>
	</two-button-dialog>
	<two-button-dialog ref="editDialog" :title="editTitle" cardStyle="max-width: 50vw; width : 50vw">
		<template v-slot="{data}">
			<q-card-section>
				<div class="items-center q-my-sm">
					<q-input v-model="data.quoteNum" bg-color="grey-3" :dark="false" filled
						label-color="primary" label="Quotation Number"
						:rules="[
							val => !!val || 'Field is required',
						]"
					/>
				</div>
			</q-card-section>
			<q-item class="text-black items-center">
				<q-btn class="q-mx-lg row items-start" color="info" @click="importQuotation()">
					<q-icon name="mdi-import" color="white" />
				</q-btn>
				<div>uploaded file : {{ file?.name }}</div>
			</q-item>
		</template>
	</two-button-dialog>
	<q-page class="q-pa-sm">
		<q-card class="bg-primary">
			<q-card-section class="row justify-between">
				<div class="text-section-color text-h6">Quotation Request</div>
			</q-card-section>
			<q-separator dark />
			<div class="row q-ma-sm">
				<div class="col-md-4 col-12" style="min-height:70vh">
					<q-scroll-area class="fit">
						<q-list class="q-ma-xs">
							<q-card v-for="(item) in items" :key="item.id">
								<q-item class="q-mx-xs q-my-sm row" clickable 
									:active="selectedItem?.id == item.id" active-class="bg-green-8" @click="changeSelectedItem(item)"
								>
									<q-item-section avatar>
										<q-icon size="sm" name="mdi-account-group" color="white" />
									</q-item-section>
									<q-item-section class="text-white text-left">{{ item.collectionName }} - {{ item.customerEmail }}</q-item-section>
									<q-item-section side>
										<q-btn flat dense round icon="mdi-dots-vertical">
											<q-menu fit class="text-no-wrap bg-accent">
												<q-list>
													<q-item v-close-popup clickable @click="editAction(item)">
														<q-item-section avatar>
															<q-icon color="white" name="mdi-rename-box" />
														</q-item-section>
														<q-item-section>edit quotation</q-item-section>
													</q-item>
													<q-item v-close-popup clickable @click="removeAction(item)">
														<q-item-section avatar>
															<q-icon color="white" name="mdi-delete" />
														</q-item-section>
														<q-item-section>delete quotation</q-item-section>
													</q-item>
												</q-list>
											</q-menu>
										</q-btn>
									</q-item-section>
								</q-item>
								<quotation-request-info-component v-if="selectedItem?.id == item.id && $q.screen.lt.md" :request="selectedItem" 
									class="q-ma-xs q-pa-sm bg-accent"
								/>
							</q-card>
						</q-list>
					</q-scroll-area>
				</div>
				<q-separator dark vertical />
				<div v-if="$q.screen.gt.sm" class="row col-grow">
					<q-separator dark vertical />
					<q-scroll-area class="col-grow q-ma-sm">
						<quotation-request-info-component v-if="selectedItem" :request="selectedItem" />	
					</q-scroll-area>
				</div>
			</div>
		</q-card>
	</q-page>
</template>

<script>
import { defineComponent, ref, onMounted } from "vue";
import { useStore } from 'vuex'
import { showFileDialog, fileToDataUrl, dataUrlToBase64String } from '../script/utils/utils.js'

import TwoButtonDialog from '../components/TwoButtonDialog.vue'
import QuotationService from "../script/services/QuotationService.js";
import * as PopupDialog from '../script/utils/PopupDialog.js'
import CurrentUserService from "../script/services/CurrentUserService.js";
import RequestService from "../script/services/RequestService.js";
import QuotationRequestInfoComponent from "../components/QuotationRequestInfoComponent.vue";
import moment from 'moment'


export default defineComponent ({
	components: {
		TwoButtonDialog,
		QuotationRequestInfoComponent
	},
	setup() {
		const items = ref([])
		const store = useStore()
		const selectedItem = ref(null)
		const file = ref(null)
		const base64Profile = ref(null)
		const currentUser = ref(null)



		const changeSelectedItem = (item) => selectedItem.value = item

		const removeDialog = ref(null)
		const removeAction = async (item) => {
			let data = await removeDialog.value.run({ name: item.name });
			if (data == null) return

			try {
				await RequestService.remove({ id : item.id });
				items.value = await RequestService.findQuotationRequest()
				selectedItem.value = null
				PopupDialog.show(store, PopupDialog.SUCCESS, 'Successfully removed quotation request');
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		const editDialog = ref(null)
		const editTitle = ref(null)
		const editAction = async (item) => {
			file.value = null
			editTitle.value = "Edit Request"
			let data = await editDialog.value.run(
				{ 
					quoteNum: item?.quoteNum,
					assigneeEmail: currentUser.value.email,
					assigneeName: currentUser.value.username,
					requestId: item.id,
				},{
					successCheck : (data) => data.quoteNum != undefined && base64Profile.value != null
				});
			if (data == null) return

			try {
				await QuotationService.create({
					quoteNum: data.quoteNum,
					assigneeEmail: data.assigneeEmail,
					assigneeName: data.assigneeName,
					base64Profile: base64Profile.value,
					requestId: data.requestId
				})
				items.value = await RequestService.findQuotationRequest()
				selectedItem.value = null
				PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully updated quotation on request")
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		const importQuotation = async () => {
			// add details
			file.value = null
			file.value = await showFileDialog(".pdf")
			if (file.value == null) return

			var url = await fileToDataUrl(file.value) 
			if (url == null) return
			base64Profile.value = dataUrlToBase64String(url)
		}

		const convertToCalendar = (time) => moment.utc(time ?? 0).format("YYYY-MM-DD");

		onMounted(async () => {
			try {
				currentUser.value = await CurrentUserService.current()
				items.value = await RequestService.findQuotationRequest()
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		})

		return { file, items, selectedItem, removeDialog, editDialog, editTitle, editAction, removeAction, convertToCalendar, 
			importQuotation, changeSelectedItem } 
	}
})
</script>