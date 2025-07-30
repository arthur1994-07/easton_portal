<template>
	<div id="q-app">
		<router-view />
	</div>
</template>

<script>
'use strict'

import { createToast } from 'mosha-vue-toastify';
import { defineComponent, onBeforeUnmount } from 'vue'
import { useQuasar } from 'quasar'
import { useStore } from 'vuex'
import 'mosha-vue-toastify/dist/style.css'

export default defineComponent ({
	setup() {
		const q = useQuasar();
		const store = useStore();

		document.title = "Easton"
		q.dark.set(true)

		store.dispatch('ui/initialiseStore', ({
		}))
		
		const unsubscribe = store.subscribe((mutation, state) => {
			if (mutation.type == 'ui/notified') {
				createToast(state.ui.messagePacket.text, {
					showIcon: true,
					transition: 'slide',
					position: 'bottom-right',
					type: state.ui.messagePacket.type,
				})
			} 
		})

		onBeforeUnmount(() => unsubscribe());
		return { }
	}
})

</script>


<style lang="scss">
#q-app {
	font-family: Avenir, Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	text-align: center;
	// color: #eeedea;
	overflow: auto;
	background: $secondary
}

.text-section-color {
	color : #B9BFF4; 
}

.bg-section-color {
	background : #B9BFF4; 
}

// grid layout system
.vue-grid-item.vue-grid-placeholder {
	background: green !important;
}
</style>
