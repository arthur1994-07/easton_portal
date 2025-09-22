<template>
	<q-card style="height: 100%">
		<div>Active Quotation</div>
		<div v-for="(item) in items" :key="item.id" class="justify-between row q-pa-sm">
			<div class="col column">
				<div class="text-left">
					<div class="col-grow text-left q-pa-md">{{ item.collectionName }}</div>
				</div>
			</div>
		</div>
	</q-card>
</template>

<script>
import { defineComponent, onMounted, ref } from "vue";
import { useStore } from 'vuex'
import RequestService from "../../script/services/RequestService.js";
import CurrentUserService from "../../script/services/CurrentUserService.js";
import * as PopupDialog from '../../script/utils/PopupDialog.js'

export default defineComponent ({
	setup() {
		const store = useStore()
		const items = ref([])

		onMounted(async () => {
			try {
				const current = await CurrentUserService.current()
				items.value = await RequestService.findQuotation({id: current.uuid})
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
			
		})

		return { items }
	}
})
</script>