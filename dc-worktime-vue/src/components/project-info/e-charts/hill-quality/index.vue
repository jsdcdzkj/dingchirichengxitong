<template>
	<div ref="chartRef" style="width: 100%; height: 200px"></div>
</template>

<script lang="ts" setup>
	import { onMounted, ref } from "vue";

	import { useEChartsResize } from "../resize";
	import { object } from "vue-types";

	const { initChart, chartRef } = useEChartsResize();
	const props = defineProps({
		data: object().def()
	});
	onMounted(() => {
		const option = {
			tooltip: {
				trigger: "axis",
				axisPointer: {
					type: "none"
				},
				formatter: function (params) {
					return params[0].name + ": " + params[0].value;
				}
			},
			xAxis: {
				data: ["项目BUG", "项目严重BUG", "评审BUG", "评审严重BUG"],
				axisTick: { show: true },
				axisLine: {
					show: true,
					lineStyle: {
						color: "#9B9B9B"
					}
				},
				axisLabel: {
					color: "#9B9B9B"
				}
			},
			// yAxis: {
			// 	splitLine: { show: true },
			// 	axisTick: { show: true },
			// 	axisLine: { show: true },
			// 	axisLabel: { show: true }
			// },
			// xAxis: {
			// 	type: "category",
			// 	boundaryGap: false,
			// 	data: ["项目BUG", "项目严重BUG", "评审BUG", "评审严重BUG"],
			// 	axisTick: {
			// 		show: false,
			// 		lineStyle: {
			// 			type: "dashed"
			// 		}
			// 	},
			// 	axisLine: {
			// 		lineStyle: {
			// 			color: "#9B9B9B"
			// 		}
			// 	}
			// },
			yAxis: {
				type: "value",
				splitLine: {
					lineStyle: {
						type: "dashed"
					}
				},
				axisLine: {
					lineStyle: {
						color: "#9B9B9B"
					}
				}
			},
			color: ["#e54035"],
			grid: {
				left: "3%",
				right: "10%",
				bottom: "3%",
				top: "10%",
				containLabel: true
			},
			series: [
				{
					name: "hill",
					type: "pictorialBar",
					barCategoryGap: "10%",

					// symbol: 'path://M0,10 L10,10 L5,0 L0,10 z',
					symbol: "path://M0,10 L10,10 C5.5,10 5.5,5 5,0 C4.5,5 4.5,10 0,10 z",
					itemStyle: {
						opacity: 0.5
					},
					emphasis: {
						itemStyle: {
							opacity: 1
						}
					},
					data: [
						props.data.source_1,
						props.data.source_1_severity_3,
						props.data.source_2,
						props.data.source_2_severity_3
					],
					z: 10
				}
			]
		};

		initChart(option);
	});
</script>
