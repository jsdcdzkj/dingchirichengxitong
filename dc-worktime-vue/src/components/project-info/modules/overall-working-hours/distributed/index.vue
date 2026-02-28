<template>
	<Tabs :tabs="list" v-model:value="current" @change="handleChange" />
	<div class="flex m-t-16px">
		<PieHoursSts :list="pieHoursData" v-if="show" :name="list[current].name" />
		<HoursCountTable :list="listData" v-if="show" />
	</div>
</template>

<script setup lang="ts">
	import { workHoursListData } from "@/api/project/info";
	import { appInstance } from "@/main";
	import { reactive, ref } from "vue";
	const pieHoursData = ref<any>([]);
	const listData = ref([] as any[]);
	const show = ref(false);
	const list = [
		{
			name: "合计工时分布",
			id: 1
		},
		{
			name: "任务工时分布",
			id: 2
		},
		{
			name: "运维工时分布",
			id: 3
		},
		{
			name: "BUG修复任务分布",
			id: 4
		}
	];
	const keys = ["sumWorkHoursTotal", "sumWorkHoursTask", "sumWorkHoursOperation", "sumWorkHoursTestBug"];
	const current = ref(0);
	const handleChange = (index: number) => {
		pieHoursData.value = listData.value
			.filter((item) => Number(item[keys[index]]) > 0)
			.map((item: any) => {
				return {
					name: item.name,
					value: item[keys[index]]
				};
			});
	};

	workHoursListData({ project_id: appInstance.projectItem.id }).then((res) => {
		console.log("appInstance.projectItem.id", appInstance.projectItem.id);
		listData.value = res;
		handleChange(0);
		show.value = true;
	});
</script>

<style scoped lang="scss"></style>
