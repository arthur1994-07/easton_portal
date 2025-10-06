import { createApp } from 'vue'
import { Quasar } from 'quasar'
import App from './App.vue'
import router from './script/plugins/vuerouter'
import store from './script/plugins/vuexstore'
import quasarUserOptions from './script/plugins/quasar-user-options'

// import global styles
// import './css/global.css'

createApp(App).use(Quasar, quasarUserOptions)
	.use(router)
	.use(store)
	.mount('#app')
