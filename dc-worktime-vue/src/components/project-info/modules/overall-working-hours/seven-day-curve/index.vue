<template>
	<div class="m-l-15px flex-1">
		<Title title="近七日偏差曲线"> </Title>
		<LineSevenDay v-if="showRef" :data="data" />
	</div>
</template>

<script setup lang="ts">
	import { workHoursDaysData } from "@/api/project/info";
	import LineSevenDay from "@/components/project-info/e-charts/line-seven-day";
	import { appInstance } from "@/main";
	import { reactive, ref } from "vue";

	const data = reactive({
		planValueList: [],
		nameList: [],
		workValueList: []
	});
	const showRef = ref(false);
	workHoursDaysData({ project_id: appInstance.projectItem.id }).then((res) => {
		Object.keys(data).forEach((key) => {
			Reflect.set(data, key, res[key]);
		});
		showRef.value = true;
	});
</script>
