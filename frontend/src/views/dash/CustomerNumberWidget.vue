<template>
	<q-card style="height: 100%; text-align: left;">
		<h3>Total Users</h3>
		<q-item>
			<q-item-section>
				<div class="user-number">2,847</div>
				<div class="user-active">Active users</div>
			</q-item-section>
			<q-item-section class="justify-center">
				<div class="circular-progress">
					<div class="progress-ring">
						<div class="progress-fill bg-primary" />
					</div>
				</div>	
			</q-item-section>
		</q-item>
	</q-card>
</template>

<script>
import { defineComponent } from "vue";

export default defineComponent ({
	setup() {
	}
})
</script>

<style>

.dashboard-card h3 {
	color: #94a8c9;
	margin: 0 0 10px 0;
	font-size: 20px;
	font-weight: 600;
}


.user-number {
	font-size: 36px;
	font-weight: 700;
	color: #a3aec2;
	margin-bottom: 8px;
}

.user-active {
	color: #a3b8d5;
	font-size: 14px;
	margin-bottom: 24px;
}

.circular-progress {
	display: flex;
	justify-content: right;
	margin-top: 20px;
}

.progress-ring {
	width: 120px;
	height: 120px;
	border-radius: 50%;
	background: conic-gradient(
		from 0deg,
		#ef4444 0deg 120deg,
		#10b981 120deg 240deg,
		#f59e0b 240deg 360deg
	);
	display: flex;
	align-items: center;
	justify-content: center;
}

.progress-fill {
	width: 70px;
	height: 70px;
	border-radius: 50%;
}


</style>