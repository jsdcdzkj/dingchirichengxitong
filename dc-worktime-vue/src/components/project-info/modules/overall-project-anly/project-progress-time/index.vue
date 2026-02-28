<template>
	<div class="flex-1">
		<Title title="测试完成进度实时动态"></Title>
		<PieTimeProgress :data="list" v-if="showRef" />
	</div>
</template>

<script setup lang="ts">
	import { completionProgress } from "@/api/project/info";
	import { appInstance } from "@/main";
	import { reactive, ref } from "vue";

	const data = reactive({
		proceed: 0, //进行数
		percent_percent: 0, //进行百分比
		start: 0, //开始数
		start_percent: 0, //开始百分比
		end: 0, //结束数
		end_percent: 0 //结束百分比
	});

	const list = ref<any>([]);

	const showRef = ref(false);
	completionProgress({ pro_project_id: appInstance.projectItem.id }).then((res) => {
		Object.keys(data).forEach((key) => {
			Reflect.set(data, key, res[key]);
		});
		list.value = [
			{
				name: "未开始",
				value: data.proceed
			},
			{
				name: "进行中",
				value: data.start
			},
			{
				name: "已完成",
				value: data.end
			}
		];
		showRef.value = true;
	});
</script>
