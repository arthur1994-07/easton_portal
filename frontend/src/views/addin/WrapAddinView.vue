<template>
	<!-- <addin-provider ref="addinProvider" :preloadActiveSite="preloadActiveSite" /> -->
	<component :is="component" v-bind="bindingData" class="dashboard-card q-mx-xs" />
</template>
<script>
'use strict'

// import AddinProvider from './AddinProvider.vue'

import { defineComponent, ref, computed, watch } from 'vue'

export default defineComponent({
	// components: {
	// 	AddinProvider,
	// },
	inheritAttrs: false,
	props: {
		component: {},
		preloadActiveSite: { default: null }
	},
	setup() {
		const addinProvider = ref(null)
		const callHandle = computed(() =>  addinProvider.value?.callHandle)
		const bindingData = ref({ callHandle })
		watch(addinProvider, () => {
			for (const [key, value] of Object.entries(addinProvider.value.referenceSet)) {
				bindingData.value[key] = value
			}
		})
		return { addinProvider, bindingData }
	}
})
</script>

<style>
.dashboard-card {
	
	border-radius: 20px;
	padding: 32px;
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
	transition: transform 0.3s;
}

.dashboard-card:hover {
	transform: translateY(-10px);
}
</style>
