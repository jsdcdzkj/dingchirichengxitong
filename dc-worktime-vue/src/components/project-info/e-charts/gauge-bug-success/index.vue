<template>
	<div ref="chartRef" style="width: 100%; height: 198px"></div>
</template>

<script lang="ts" setup>
	import { nextTick, onMounted, watch } from "vue";
	import { useEChartsResize } from "../resize";
	import { number } from "vue-types";

	// const chartRef = ref<HTMLDivElement | null>(null);

	const { initChart, chartRef, chartInstance, setChartOption } = useEChartsResize();
	const props = defineProps({
		data: number().def(0),
		type: number().def(0)
	});
	watch(
		() => props.type,
		() => {
			const option = {
				title: {
					text: `${props.data}%`,
					left: "center",
					top: "middle",
					textStyle: {
						color: "#5878CB",
						fontSize: 20,
						fontWeight: 600
					},
					textAlign: "auto"
				},
				series: [
					{
						type: "gauge",
						startAngle: 210,
						endAngle: -30,
						min: 0,
						max: 100,
						splitNumber: 12,
						itemStyle: {
							color: "#58D9F9",
							shadowColor: "rgba(0,138,255,0.45)",
							shadowBlur: 10,
							shadowOffsetX: 2,
							shadowOffsetY: 2
						},
						progress: {
							show: true,
							roundCap: true,
							width: 18
						},
						pointer: {
							icon: "path://M2090.36389,615.30999 L2090.36389,615.30999 C2091.48372,615.30999 2092.40383,616.194028 2092.44859,617.312956 L2096.90698,728.755929 C2097.05155,732.369577 2094.2393,735.416212 2090.62566,735.56078 C2090.53845,735.564269 2090.45117,735.566014 2090.36389,735.566014 L2090.36389,735.566014 C2086.74736,735.566014 2083.81557,732.63423 2083.81557,729.017692 C2083.81557,728.930412 2083.81732,728.84314 2083.82081,728.755929 L2088.2792,617.312956 C2088.32396,616.194028 2089.24407,615.30999 2090.36389,615.30999 Z",
							length: "75%",
							width: 16,
							offsetCenter: [0, "5%"]
						},
						axisLine: {
							roundCap: true,
							lineStyle: {
								width: 18
							}
						},
						axisTick: {
							show: false,
							splitNumber: 2,
							lineStyle: {
								width: 2,
								color: "#999"
							}
						},
						splitLine: {
							show: true,
							length: 12,
							distance: -40,
							lineStyle: {
								width: 1,
								color: "#eee"
							}
						},
						axisLabel: {
							distance: 30,
							show: false,
							color: "#999",
							fontSize: 12
						},
						title: {
							show: false
						},
						detail: {
							backgroundColor: "#fff",
							width: "60%",
							lineHeight: 20,
							height: 40,
							offsetCenter: [0, "80%"],
							valueAnimation: true,

							formatter: function (value) {
								let text = "全部";
								if (props.type == 1) {
									text = "前端";
								} else if (props.type == 2) {
									text = "后端";
								}
								return `${text}BUG\n修复完成度`;
							},
							textStyle: {
								color: "#999",
								fontSize: 12
							}
						},
						data: [
							{
								value: props.data
							}
						]
					},
					{
						type: "pie",
						center: ["50%", "50%"],
						radius: "30%",
						hoverAnimation: false,
						itemStyle: {
							normal: {
								shadowBlur: 200,
								shadowColor: "rgba(0, 0, 0, 0.5)",
								color: "#fff"
							}
						},
						data: [100]
					},
					{
						type: "pie",
						center: ["50%", "50%"],
						radius: ["84%", "85%"],
						hoverAnimation: false,
						label: {
							show: false
						},
						itemStyle: {
							normal: {
								shadowBlur: 200,
								shadowColor: "rgba(0, 0, 0, 0.5)",
								color: "#eee"
							}
						},
						data: [100]
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
		{
			immediate: true
		}
	);
</script>

<style scoped></style>
