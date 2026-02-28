<template>
	<Title title="高风险任务预警"> </Title>
	<Carousel :list="list" class="m-t-10px" h="342px" />
</template>

<script setup lang="tsx">
	import { highRiskTaskList } from "@/api/project/info";
	import Carousel from "@/components/module/project/carousel/index.vue";
	import { appInstance } from "@/main";
	import { ref } from "vue";
	const list = ref<any>([]);
	function calculateTimeRemaining(totalSeconds: number) {
		if (totalSeconds < 60) {
			return { h: 0, m: totalSeconds };
		}
		const h = Math.floor(totalSeconds / 60);
		const m = Math.floor(totalSeconds % 60);
		return { h, m };
	}

	highRiskTaskList(appInstance.projectItem.id).then((res) => {
		const listMap: any[] = res.map((item: any) => {
			let { h, m } = calculateTimeRemaining(item.postponeTime || 0);
			const task_attribute = appInstance.mapDict.task_attribute?.find(
				(it) => it.value == item.task_attribute
			)?.label;

			return {
				personChargeName: item.personChargeName,
				startTime: item.startTime,
				endTime: item.endTime,
				isPostpone: item.isPostpone,
				postponeTime: item.postponeTime,
				title: item.title,
				task_attribute,
				postPoneTemp: h > 0 ? `${h}小时 ${m}分钟` : `${m}分钟`
			};
		});
		for (let i = 0; i < listMap.length; i += 3) {
			list.value.push(listMap.slice(i, i + 3));
		}
	});
</script>
