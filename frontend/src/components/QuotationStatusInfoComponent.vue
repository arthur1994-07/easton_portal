<template>
	<q-list class="column no-wrap">
		<q-card-section class="justify-between">
			<div class="col column">
				<div class="text-left">
					<div class="col-grow text-left q-pa-md">Collection Name : {{ quotation.collectionName }}</div>
					<div class="col-grow text-left q-pa-md">Assignee Email : {{ quotation.customerName }}</div>
					<div class="col-grow text-left q-pa-md">Created Date : {{ convertToCalendar(quotation.createdDate) }}</div>
				</div>
			</div>
			<div class="col-grow text-left q-pa-md">
				Download Quotation: 
				<q-btn class="row items-star" color="secondary" @click="downloadQuotation(quotation)">
					<q-icon name="mdi-cloud-download" color="white" />
					<q-tooltip>Download file</q-tooltip>
				</q-btn>
			</div>
		</q-card-section>
	</q-list>
</template>

<script>
'use strict'

import { defineComponent } from 'vue'
import { base64ToUint8Array } from '../script/utils/utils.js'

import moment from 'moment'


export default defineComponent({
	props: {
		quotation: { required: true },
	},
	setup() {

		const convertToCalendar = (time) => moment.utc(time ?? 0).format("YYYY-MM-DD");

		const downloadQuotation = (item) => {
			const data = base64ToUint8Array(item.quotation.document)
			const url = window.URL.createObjectURL(new Blob([data]));
			const link = document.createElement('a');

			link.href = url;
			link.setAttribute('download', `quotation.pdf`)
			link.setAttribute("style", "display: none")
			document.body.appendChild(link);
			link.click();
		}

		
		return { downloadQuotation, convertToCalendar }

	}
})
</script>