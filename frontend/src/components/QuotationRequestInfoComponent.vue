<template>
	<q-list class="column no-wrap">
		<q-card-section class="justify-between">
			<div class="col column">
				<div class="text-left">
					<div class="col-grow text-left q-pa-md">Requester Email : {{ request.customerEmail }}</div>
					<div class="col-grow text-left q-pa-md">Requester Name : {{ request.customerName }}</div>
					<div class="col-grow text-left q-pa-md">Requested Date : {{ convertToCalendar(request.createdDate) }}</div>
					<div class="col-grow text-left q-pa-md">Requester Collection : {{ request.collectionName }}</div>
				</div>
			</div>
		</q-card-section>
	</q-list>
</template>

<script>
'use strict'

import { defineComponent, } from 'vue'
import moment from 'moment'


export default defineComponent({
	props: {
		request: { required: true },
	},
	setup() {

		const convertToCalendar = (time) => moment.utc(time ?? 0).format("YYYY-MM-DD");

		return { convertToCalendar }

	}
})
</script>