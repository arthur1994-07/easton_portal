<template>
    <div>Addin</div>
</template>
<script>
'use strict'

import { ref, defineComponent, toRefs, computed } from 'vue'
import { useStore } from 'vuex'
// import * as PopupDialog from '../../../script/utils/PopupDialog.js'


export default defineComponent({

	props: {
		preloadActiveSite : { default: null }
	},
	setup(props) {
		const { t } = useI18n()
		const store = useStore()
		const { preloadActiveSite } = toRefs(props);

		const currentActiveSite = ref(null)
		const activeSite = computed(() => preloadActiveSite.value ?? currentActiveSite.value)



		return { }
	}
})
</script>