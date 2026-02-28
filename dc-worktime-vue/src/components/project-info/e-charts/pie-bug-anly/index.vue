<template>
	<div ref="chartRef" style="width: 100%; height: 370px"></div>
</template>

<script lang="ts" setup>
	import { nextTick, onMounted, watch } from "vue";
	import { useEChartsResize } from "../resize";
	import { array } from "vue-types";

	const { chartRef, initChart, chartInstance, setChartOption } = useEChartsResize();
	const props = defineProps({
		data: array().def([])
	});
	watch(
		() => props.data,
		() => {
			const option: any = {
				legend: {
					bottom: "10"
				},
				tooltip: {},
				series: [
					{
						name: "组内bug占比",
						type: "pie",
						radius: [20, "80%"],
						center: ["50%", "44%"],
						roseType: "area",
						itemStyle: {
							borderRadius: 8
						},
						label: false,
						data: props.data
					}
				]
			};
			nextTick(() => {
				if (chartInstance) {
					setChartOption(option);
				} else {
					initChart(option);
				}
			});
		},
		{ immediate: true }
	);
</script>
