<template>
	<ElCard class="box-card mt-16px flex-1">
		<template #header>
			<div class="card-header flex flex-justify-between">
				<span>小组工时总览</span>
			</div>
		</template>
		<div class="flex">
			<HoursAbsTeam :groupId="props.groupId" :projectId="props.projectId" @date-changed="handleDateChange" />
			<SevenDayCurveTeam :groupId="props.groupId" :projectId="props.projectId" />
		</div>
		<DistributedTeam
			:groupId="props.groupId"
			:projectId="props.projectId"
			:startDate="startDate"
			:endDate="endDate"
		/>
	</ElCard>
</template>
<script setup lang="tsx">
	import { ref, onMounted, computed, nextTick, watch } from "vue";

	const props = defineProps({
		groupId: {
			type: Number,
			default: 0
		},
		projectId: {
			type: Number,
			default: 0
		}
	});

	const startDate = ref("");
	const endDate = ref("");

	const handleDateChange = (newStartDate: string, newEndDate: string) => {
		startDate.value = newStartDate;
		endDate.value = newEndDate;
	};
</script>
