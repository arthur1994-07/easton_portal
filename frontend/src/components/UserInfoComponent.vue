<template>
	<q-list class="column no-wrap">
		<div class="text-left q-pa-xs">
			User : {{ user.accountName ?? user.email }}
			<q-chip v-if="user.disable" color="red" size="sm" outline>
				Disabled
			</q-chip>
		</div>
		<div class="row">
			<div class="col-grow text-left q-pa-xs">Email : {{ user.email }}</div>
			<div class="col-grow text-left q-pa-xs">Username : {{ user.username }}</div>
		</div>
		<div class="text-left q-pa-xs">Roles : {{ (user.administrator ? 1 : 0) + user.roles.length }} Count</div>
		<div v-if="user.administrator" class="row text-left q-pa-xs">
			<q-icon class="q-px-md" color="white" size="xs" name="mdi-account-group" />
			<div class="text-left text-blue">Administrator</div>
		</div>
		<div v-for="(item) in user.roles" :key="item.id" class="row text-left q-pa-xs">
			<q-icon class="q-px-md" color="white" size="xs" name="mdi-account-group" />
			<div class="text-left text-blue">{{ item.name }}</div>
		</div>
	</q-list>
</template>
<script>
'use strict'

import { defineComponent, } from 'vue'

export default defineComponent({
	props: {
		user: { required: true },
	},
	setup() {

	}
})
</script>