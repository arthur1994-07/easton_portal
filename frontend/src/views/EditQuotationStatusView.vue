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
	<q-page class="q-pa-sm">
		<q-card class="bg-primary">
			<q-card-section class="row justify-between">
				<div class="text-section-color text-h6">Quotation Status</div>
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
								<quotation-status-info-component v-if="selectedItem?.id == item.id && $q.screen.lt.md" :quotation="selectedItem" 
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
						<quotation-status-info-component v-if="selectedItem" :quotation="selectedItem" />	
					</q-scroll-area>
				</div>
			</div>
		</q-card>
	</q-page>
</template>

<script>
import { defineComponent, onMounted, ref } from "vue";
import { useStore } from 'vuex'

import RequestService from "../script/services/RequestService.js";
import TwoButtonDialog from '../components/TwoButtonDialog.vue'
import * as PopupDialog from '../script/utils/PopupDialog.js'
import QuotationStatusInfoComponent from "../components/QuotationStatusInfoComponent.vue";

export default defineComponent ({
	components: {
		TwoButtonDialog,
		QuotationStatusInfoComponent	
	},
	setup() {
		const store = useStore()
		const items = ref([])
		const selectedItem = ref(null)

		
		const removeDialog = ref(null)
		const removeAction = async (item) => {
			let data = await removeDialog.value.run({ name: item.name });
			if (data == null) return

			try {
				await RequestService.remove({ id : item.id });
				items.value = await RequestService.list()
				selectedItem.value = null
				PopupDialog.show(store, PopupDialog.SUCCESS, 'Successfully removed quotation');
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		const changeSelectedItem = (item) => selectedItem.value = item


		onMounted(async () => {
			items.value = await RequestService.findQuotation()
		})

		return { items, removeDialog, selectedItem, removeAction, changeSelectedItem }
	}
})
</script>