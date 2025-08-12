<template>
	<q-dialog v-model="dialog" :full-height="fullHeight ?? false" :full-width="fullWidth ?? false">
		<q-card class="bg-white column no-wrap" :class="cardClass">
			<q-card-section>
				<div class="text-h6 text-primary">{{ title }}</div>
			</q-card-section>
			<q-separator dark />
			<slot :data="externalData" />
			<q-separator dark />

			<q-card-actions align="right">
				<q-btn v-close-popup push :label="cancelText" color="white" text-color="primary" />
				<q-btn push :label="successText" color="primary" :disable="!successAllow" @click="acceptAction" />
			</q-card-actions>
		</q-card>
	</q-dialog>
</template>

<script>
import { ref } from 'vue'
import { computed, watch, defineComponent } from 'vue'

export default defineComponent({
	props: {
		title : { type: String, default: "Dialog" },
		cardClass : { type: String, default: "dialog"}, 
		successText : { type: String, default : "Accept"} ,
		cancelText: { type: String, default: "Cancel" },
		fullWidth : { type: Boolean, default : false },
		fullHeight : { type : Boolean, default : false },
	},
	setup() {
		const dialog = ref(false)
		const externalData = ref(null)
		const returnData = ref(null)
		const successCheckCb = ref(null)
		const successAllow = computed(() => successCheckCb.value?.(externalData.value) ?? true)

		const run = async (data, property) => {
			successCheckCb.value = property?.successCheck
			returnData.value = null
			externalData.value = data
			dialog.value = true

			return await new Promise((resolve) => {
				const stop = watch(dialog, () => {
					resolve(returnData.value)
					stop();
				})
			})
		}

		const acceptAction = () => {
			returnData.value = externalData.value
			dialog.value = false
		}

		return {dialog, externalData, successAllow, run, acceptAction}
	}
})
</script>
<style lang="scss" scoped>

// responsive handle

body.screen--xs {
	.dialog {
		width: 95vw
	}
}
body.screen--sm {
	.dialog {
		width: 80vw
	}
}
body.screen--md {
	.dialog {
		width: 60vw
	}
}
.dialog {
	width: 50vw
}
</style>
