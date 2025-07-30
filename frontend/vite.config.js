import { defineConfig } from 'vite'
import { fileURLToPath } from 'url'
import vue from '@vitejs/plugin-vue'
import eslint from 'vite-plugin-eslint'
import { quasar, transformAssetUrls } from '@quasar/vite-plugin'

// https://vitejs.dev/config/
export default defineConfig({
	define: {
		'process.env': {}
	},
	plugins: [vue({
		template: { transformAssetUrls }
	}),
	quasar({
		sassVariables: 'src/css/quasar.variables.sass'
	}),
	eslint()
	],
	server: {
		port: 7070
	},
	resolve: {
		alias: {
			'@': fileURLToPath(new URL('./src', import.meta.url)),
		},
	},
})
