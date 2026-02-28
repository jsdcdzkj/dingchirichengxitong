<template>
	<div ref="chartRef" style="width: 356px; height: 220px"></div>
</template>

<script lang="ts" setup>
	import { ref, onMounted } from "vue";
	import * as echarts from "echarts";
	import { number } from "vue-types";

	const props = defineProps({
		data: number()
	});
	const chartRef = ref<HTMLDivElement | null>(null);

	onMounted(() => {
		if (chartRef.value) {
			const chartInstance = echarts.init(chartRef.value);
			const option = {
				tooltip: {
					trigger: "item"
				},
				legend: { bottom: "0", left: "center" },
				series: [
					{
						name: "测试完成进度实时动态",
						type: "pie",
						radius: ["50%", "100%"],
						center: ["50%", "70%"],
						padAngle: 2,
						startAngle: 180,
						label: {
							show: false
						},
						endAngle: 360,
						data: props.data
					}
				]
			};
			chartInstance.setOption(option);
		}
	});
</script>
