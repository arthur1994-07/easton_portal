<template>
	<q-list class="column no-wrap">
		<div v-for="(item) in rights" :key="item.id" class="justify-between row q-pa-sm">
			<div class="text-h6 text-white text-left self-center">{{ item.displayKey }}</div>
			<q-checkbox v-if="editMode" color="teal" :modelValue="editingSet[item.id]" @update:modelValue="(value) => changeValue(item.id, value)" />
			<q-icon v-else-if="hasRight(item)" class="q-pa-xs" color="green" size="md" name="mdi-check" />
			<q-icon v-else class="q-pa-xs" color="red" size="md" name="mdi-close" />
		</div>
	</q-list>
</template>
<script>
'use strict'

import { defineComponent, toRefs } from 'vue'
// import { useI18n } from 'vue-i18n'

export default defineComponent({
	props: {
		role: { required: true },
		rights: { required: true },
		editingSet: { required: true },
		editMode : { type: Boolean, default : false, },
	},
	emits : [
		'update:editingSet',
	],
	setup(props, context) {
		// const { t } = useI18n()
		const { role } = toRefs(props)
		
		const changeValue = (key, value) => {
			context.emit('update:editingSet', key, value)
		}

		const hasRight = (item) => {
			if (role.value == null) return false
			return role.value.rights.find(s => s == item.id) != null
		}
		return { changeValue, hasRight }
	}
})
</script>