<template>
	<two-button-dialog ref="dialog" :title="title" :successText="successText" :cardClass="cardClass">
		<template v-slot="{data}">
			<q-card-section>
				<div v-if="data.items.length == 0" class="text-primary text-center">
					<slot name="no-data" :data="data">
						No Data
					</slot>
				</div>
				<q-scroll-area v-else class="row full-width" style="height:50vh">
					<q-list class="q-ma-xs">
						<q-card v-for="(item, index) in data.items" :key="index" class="q-ma-xs">
							<q-item clickable 
								:active="isSelected(data, item, index)" :active-class="`bg-${selectedColor}`" @click="selectItem(data, item, index)"
							>
								<slot :data="item" />
							</q-item>
						</q-card>
					</q-list>
				</q-scroll-area>
			</q-card-section>
			<slot name="bottom" :data="data" />
		</template>
	</two-button-dialog>
</template>

<script>
import { ref, toRefs } from 'vue'
import { defineComponent } from "vue"
import TwoButtonDialog from './TwoButtonDialog.vue'

export default defineComponent({
	components: {
		TwoButtonDialog
	},
	props: {
		title: { required: true, type: String},
		cardClass: { type: String , default: "dialog" },
		selectedColor: {type: String, default:"blue-6" },
		selectMultiple: { type: Boolean, default: false },
		successText: { type: String, default:"Select"},
	},
	setup(props) {
		const {selectMultiple } = toRefs(props)
		const dialog = ref(null)

		const run = async (data, property) => {
			const result = await dialog.value.run({
				...data,
				selectedIndices : [...(property?.selectedIndices ?? [])],
			}, {
				successCheck : (property?.supportNoSelection ?? false) ? null : (data) => data.selectedIndices.length != 0
			})
			if (result == null) return null

			var selected = result.selectedIndices.map(s => data.items[s])
			result.selectedIndices = undefined
			result.selection = selectMultiple.value ? selected : (selected.length == 0 ? null : selected[0])
			return result
		}

		
		const dataList = (data) => { return data.items }
		const selectItem = (data, item, index) => {
			if (!selectMultiple.value) {
				data.selectedIndices = data.selectedIndices.includes(index) ? [] : [index]
				return
			}
			const pos = data.selectedIndices.indexOf(index)
			if (pos < 0) {
				data.selectedIndices.splice(0, 0, index)
			} else {
				data.selectedIndices.splice(pos, 1)
			}
		}
		const isSelected = (data, item, index) => data.selectedIndices.includes(index)
		return { dialog, run, selectItem, isSelected, dataList }
	}
})
</script>
