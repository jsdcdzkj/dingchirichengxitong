<template>
	<ElCard header="当日任务总览" class="custom-header">
		<div class="flex">
			<TodayTaskSts @change-active="handleChange" />
			<TodayTaskTable :type="type" />
		</div>
	</ElCard>
</template>

<script setup lang="ts">
	import { ref } from "vue";

	const type = ref(1);
	const handleChange = (e: any) => {
		type.value = e.type;
	};
</script>
