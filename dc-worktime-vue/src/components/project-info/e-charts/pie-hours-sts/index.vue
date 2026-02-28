<template>
	<div ref="chartRef" style="width: 342px; height: 326px"></div>
</template>

<script lang="ts" setup>
	import { nextTick, watch } from "vue";

	import { array, string } from "vue-types";
	import { useEChartsResize } from "../resize";

	const { chartInstance, chartRef, setChartOption, initChart } = useEChartsResize();

	const props = defineProps({
		list: array().def([]),
		name: string().def("")
	});

	watch(
		() => props.list,
		() => {
			const option = {
				tooltip: {
					trigger: "item"
				},
				legend: {
					orient: "vertical",
					left: "left",
					top: "middle"
				},
				label: {
					show: false
				},
				series: [
					{
						name: props.name,
						type: "pie",
						radius: ["40%", "70%"],
						padAngle: 5,
						labelLine: {
							show: false
						},
						label: {
							show: false,
							position: "center"
						},
						left: 90,
						data: props.list,
						emphasis: {
							itemStyle: {
								shadowBlur: 10,
								shadowOffsetX: 0,
								shadowColor: "rgba(0, 0, 0, 0.5)"
							}
						}
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

<style scoped></style>
