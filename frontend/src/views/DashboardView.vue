<template>
	<q-page>
		<q-separator class="q-my-sm" dark />
		<grid-layout
			v-model:layout="layout"
			:col-num="12"
			:row-height="rowHeight"
			:margin="gridMargin"
			:is-draggable="editMode"
			:is-resizable="editMode"
			:vertical-compact="!editMode"
			:use-css-transforms="true"
			:prevent-collision="!editMode"
			:is-bounded="true"
			:auto-size="true"
		>
			<grid-item v-for="(item, index) in layout" :key="index"
				class="touch-handle" 
				:i="item.i"
				:x="item.x" :y="item.y" :w="item.w" :h="item.h"
				:minH="calcMinHeight(item.widget.minHeight)" :maxH="calcMaxHeight(item.widget.maxHeight)"
				:minW="item.widget.minWidth ?? 1" :maxW="item.widget.maxWidth ?? 12"
			>
				<wrap-addin-view :component="item.widget.creator" :preloadActiveSite="activeSite" />
				<span v-if="editMode" class="vue-grid-remove">
					<q-btn dense round color="red-5" icon="mdi-close-thick" @click="deleteItem(index)">
						<q-tooltip>{{ t('dashboard_view_remove_item') }}</q-tooltip>
					</q-btn>
				</span>
			</grid-item>
		</grid-layout>
	</q-page>
</template>

<script>
import { defineComponent, ref, readonly, watch, onMounted, shallowRef, computed, defineAsyncComponent } from "vue";
import { useQuasar } from 'quasar'
import { useStore } from 'vuex'
import { useCookies } from 'vue3-cookies'
import { GridLayout, GridItem } from "vue3-grid-layout"
import { getDashWidgets } from "../script/core/addin/AddinSystem.js";
import * as PopupDialog from '../script/utils/PopupDialog.js'
import * as SettingAttribute from '../script/enums/SettingAttribute.js'
import WrapAddinView from "./addin/WrapAddinView.vue";
import * as PermissionType from '../script/enums/PermissionType.js'
import CurrentUserService from "../script/services/CurrentUserService.js";



const saveDashboardKey = 'dashboard-layout'

const defaultLayoutLarge = [
	{ x:1, y :0,  w:10, h:10,  i :"notifications", auth: null },
	{ x:1, y :10, w:10,  h:10,  i :"announcement", auth: null },
	{ x:6, y :12, w:5,  h:12,  i :"total-customers", auth: PermissionType.USER },
	{ x:1, y: 12, w:5,  h:12,  i : "active-quotation-requests", auth: PermissionType.USER }
]

const defaultLayoutSmall = [
	{ x:1, y :0,  w:10, h:10,  i :"notifications", auth: null },
	{ x:1, y :10, w:10,  h:10,  i :"announcement", auth: null },
	{ x:1, y :12, w:10,  h:12,  i :"total-customers", auth: PermissionType.USER },
	{ x:1, y: 20, w:10,  h:12,  i : "active-quotation-requests", auth: PermissionType.USER }
]


const defaultLayoutList = [defaultLayoutLarge, defaultLayoutLarge, defaultLayoutLarge, defaultLayoutSmall, defaultLayoutSmall]
const layoutTypeName = ['xl', 'lg', 'md', 'sm', 'xs']

// const filterLayout = (obj) => ({
// 	x: obj.x, y: obj.y, w: obj.w, h: obj.h, i: obj.i
// })

export default defineComponent ({
	components: {
		GridLayout,
		GridItem,
		WrapAddinView
	},
	setup() {
		const store = useStore()
		const { cookies } = useCookies()
		const q = useQuasar()
		const editMode = ref(false)
		const activeSite = ref(null)
		const gridMargin = readonly(ref([5, 5]))
		const rowHeight = readonly(ref(20))
		const userRight = ref(false)
		const calcMinHeight = (value) => value == null ? 1 : 
			Math.ceil((value + gridMargin.value[1]) / (rowHeight.value + gridMargin.value[1])) >> 0 
		const calcMaxHeight = (value) => value == null ? Infinity : 
			Math.floor((value + gridMargin.value[1]) / (rowHeight.value + gridMargin.value[1])) >> 0 
		
		const defaultWidgets = ref([
			{
				key: "notifications",
				creator: shallowRef(defineAsyncComponent(() => import('../views/dash/NotificationWidget.vue'))),
				minHeight: 150,
				maxHeight: 280,
				auth: null,
			},
			{
				key: "announcement",
				creator: shallowRef(defineAsyncComponent(() => import('./dash/NewsAnnouncementWidget.vue'))),
				minHeight: 150,
				maxHeight: 4,
				auth: null,
			},
			{
				key: "total-customers",
				creator: shallowRef(defineAsyncComponent(() => import('../views/dash/CustomerNumberWidget.vue'))),
				minHeight: 150,
				maxHeight: 4,
				auth: PermissionType.USER
			},
			{
				key: "active-quotation-requests",
				creator: shallowRef(defineAsyncComponent(() => import('../views/dash/ActiveQuotationsWidget.vue'))),
				minHeight: 150,
				maxHeight: 4,
				auth: PermissionType.USER
			},
		])

		const layoutType = computed(() => {
			if (q.screen.xl) return 0
			if (q.screen.lg) return 1
			if (q.screen.md) return 2
			if (q.screen.sm) return 3
			return 4
		})

		const currentLayoutName = computed(() => layoutTypeName[layoutType.value])

		// localLayout
		const getLocalLayoutFromCookies = (id) => cookies.get(saveDashboardKey + '-' + layoutTypeName[id]) 
		var localLayout = ref([...Array(layoutTypeName.length).keys()].map(s => getLocalLayoutFromCookies(s)?.data))
		watch(localLayout, () => {
			const id = layoutType.value
			const key = saveDashboardKey + '-' + layoutTypeName[id]
			if (localLayout.value[id] == null) {
				cookies.remove(key)
			} else {
				cookies.set(key,{ data: localLayout.value[id]})
			}
		})

		const getLocalLayout = (id) => {
			var currentDefault = defaultLayoutList[id]
			for(var i=id+1; i>0; i--) {
				if (currentDefault != defaultLayoutList[i-1]) continue
				if (localLayout.value[i-1] != null) return localLayout.value[i-1]
			}
			return null
		}

		// server layout
		var serverLayout = ref([...Array(layoutTypeName.length).keys()].map(() => null))
		const getServerLayout = (id) => {
			var currentDefault = defaultLayoutList[id]
			for(var i=id+1; i>0; i--) {
				if (currentDefault != defaultLayoutList[i-1]) continue
				if (serverLayout.value[i-1] != null) return serverLayout.value[i-1]
			}
			return null
		}
		
		// layout settings
		const getFromWidgets = (key) => defaultWidgets.value.find(s => s.key == key)
		const safeLayoutCheck = (data) => {
			return data?.filter(s => getFromWidgets(s.i) != null)
		}
		const applyWidget = (data) => data.map(s => ({...s, widget: getFromWidgets(s.i)}))

		const defaultLayoutSettings = computed(() => defaultLayoutList[layoutType.value])
		const defaultLayout = computed(() => safeLayoutCheck(getServerLayout(layoutType.value) ?? defaultLayoutSettings.value))

		
		const currentLayout = computed(() => safeLayoutCheck(getLocalLayout(layoutType.value) ?? defaultLayout.value))
		const layout = ref(applyWidget(currentLayout.value.map(s => ({...s}))))

		watch(layoutType, () => {
			if (!userRight.value) {
				const list = currentLayout.value.filter(s => s.auth == null)
				layout.value = applyWidget(list.map(s => ({...s})));
			} else {
				layout.value = applyWidget(currentLayout.value.map(s => ({...s}))) 
			}
		})
		
		const serverDatabaseKey = (s) => SettingAttribute.PREFIX_DASHBOARD_LAYOUT + "_" + layoutTypeName[s]
		const filterServerLayout = (data) => {
			return [...Array(layoutTypeName.length).keys()].map(s => data.find(t => t.key == serverDatabaseKey(s))?.value)
		}
		
		onMounted(async () => {
			try {
				// serverLayout.value = filterServerLayout(serverLayoutData)
				const rights = await CurrentUserService.permission()
				userRight.value = rights.includes(PermissionType.USER)
				if (!userRight.value) 
					layout.value = layout.value.filter(s => s.widget.auth == null)
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		})

		// addin stuff
		const addins = computed(() => (store.state.system.addins ?? []).map(s => getDashWidgets(s)).flatMap(s => s))
		const addinsLoad = (items) => {
			items.forEach(s => {
				try {
					const { elementKey, item, register } = s;

					var index = defaultWidgets.value.findIndex(s => s.key == elementKey)
					if (index >= 0) defaultWidgets.value.splice(index, 1)

					register()
					const props = item.createProps?.call(null) ?? {}
					defaultWidgets.value.push({
						key: elementKey,
						display: item.getTitleName(),
						creator : elementKey,
						...props
					})
				} catch (err) {
					console.error(err)
				}
			})
			layout.value = applyWidget(currentLayout.value.map(s => ({...s})))
		}
		watch(addins, () => addinsLoad(addins.value))
		addinsLoad(addins.value)


		return { editMode, activeSite, rowHeight, gridMargin, calcMinHeight, calcMaxHeight, filterServerLayout, layout, currentLayoutName } 
	}
})
</script>

<style lang="scss">
.vue-grid-item > .vue-resizable-handle {
	position: absolute;
	width: 20px;
	height: 20px;
	bottom: 0;
	right: 0;
	z-index: 5;
	//background: url('data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBzdGFuZGFsb25lPSJubyI/Pg08IS0tIEdlbmVyYXRvcjogQWRvYmUgRmlyZXdvcmtzIENTNiwgRXhwb3J0IFNWRyBFeHRlbnNpb24gYnkgQWFyb24gQmVhbGwgKGh0dHA6Ly9maXJld29ya3MuYWJlYWxsLmNvbSkgLiBWZXJzaW9uOiAwLjYuMSAgLS0+DTwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+DTxzdmcgaWQ9IlVudGl0bGVkLVBhZ2UlMjAxIiB2aWV3Qm94PSIwIDAgNiA2IiBzdHlsZT0iYmFja2dyb3VuZC1jb2xvcjojZmZmZmZmMDAiIHZlcnNpb249IjEuMSINCXhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHhtbDpzcGFjZT0icHJlc2VydmUiDQl4PSIwcHgiIHk9IjBweCIgd2lkdGg9IjZweCIgaGVpZ2h0PSI2cHgiDT4NCTxnIG9wYWNpdHk9IjAuMzAyIj4NCQk8cGF0aCBkPSJNIDYgNiBMIDAgNiBMIDAgNC4yIEwgNCA0LjIgTCA0LjIgNC4yIEwgNC4yIDAgTCA2IDAgTCA2IDYgTCA2IDYgWiIgZmlsbD0iIzAwMDAwMCIvPg0JPC9nPg08L3N2Zz4=');
	background-position: bottom right;
	padding: 0 3px 3px 0;
	background-repeat: no-repeat;
	background-origin: content-box;
	box-sizing: border-box;
	cursor: se-resize;
}

</style>
<style scoped lang="scss">
.control-pane {
	background: $amber-3;
}
.vue-grid-layout {
	background: $secondary;
}

.touch-handle {
	-ms-touch-action: auto;
	touch-action: auto;
}

.vue-grid-remove {
	position: absolute;
	right: 5px;
	top: 5px;
	cursor: pointer;
}
</style>