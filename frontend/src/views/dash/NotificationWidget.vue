<template>
	<q-card style="height: 100%">
		<div class="text-h4">Notifications</div>
		<div class="justify-between row q-pa-sm">
			<div class="text-left">
				<div class="justify-between row">	
					<q-icon name="mdi-alert" color="red-4" size="xl"></q-icon>
					<div class="col-grow text-left q-pa-md">{{ requestNot }}</div>
				</div>
				<div class="justify-between row">	
					<q-icon name="mdi-alert" color="red-4" size="xl"></q-icon>
					<div class="col-grow text-left q-pa-md">{{ quotationNot }}</div>
				</div>
			</div>
		</div>
	</q-card>
</template>

<script>
import { defineComponent, onMounted, ref, computed } from "vue";
import NotificationService from "../../script/services/NotificationService.js";
import * as NotificationType from "../../script/enums/NotificationType.js"

export default defineComponent ({
	setup() {
		const items = ref([])
		const notifications = ref(null)
		const requestNot = computed(() => generateNotificationMsg(NotificationType.REQUEST) ? 'You have new Quotation requests' : '')
		const quotationNot = computed(() => generateNotificationMsg(NotificationType.REPLY) ? 'You have new Quotation message' : '')

		const generateNotificationMsg = (id) => {
			if (notifications.value == null) return
			const items = notifications.value.filter(s => s.id == id)
			return items.length != 0
		}

		onMounted(async () => notifications.value = await NotificationService.getData())

		return { items, notifications, requestNot, quotationNot }
	}
})
</script>