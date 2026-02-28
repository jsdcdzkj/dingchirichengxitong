<template>
	<div class="pie-progress" w="208px" h="208px" relative>
		<ProjectInfoIcon name="icon-question.png" :width="24" :height="24" class="absolute top-10px right-10px" />
		<div class="absolute top-0px left-0px w-full" h="208px" id="pie-progress" ref="pieContainer"></div>
	</div>
</template>

<script setup lang="ts">
	import { ProjectInfoIcon } from "@/components/icon/icon";
	import { onMounted, ref } from "vue";
	import * as Echarts from "echarts";
	import { appInstance } from "@/main";
	const pieContainer = ref<HTMLDivElement>();
	let pieContainerChart: Echarts.ECharts | null = null;
	// const { underwayTask, finishedTask, notStartTask } = appInstance.projectItem;

	const { projectSchedule } = defineProps({
		underwayTask: {
			type: Number,
			default: 0
		},
		finishedTask: {
			type: Number,
			default: 0
		},
		notStartTask: {
			type: Number,
			default: 0
		},
		projectSchedule: {
			type: Number,
			default: 0
		}
	});

	let option = {
		title: {
			text: `${projectSchedule}%`,
			left: "center",
			top: "middle",
			textStyle: {
				color: "#222",
				fontSize: 20,
				fontWeight: 600
			},
			textAlign: "auto"
		},
		grid: {
			bottom: "50%"
		},
		series: [
			{
				type: "pie",
				radius: "40%",
				hoverAnimation: false,
				animation: false,
				clockWise: false,
				itemStyle: {
					normal: {
						shadowColor: "rgba(124, 187, 235, 0.6)",
						color: "#fff"
					}
				},
				data: [100],
				label: {
					show: false
				}
			},
			{
				type: "pie",
				radius: ["40%", "74%"],
				hoverAnimation: false,
				startAngle: 90,

				legend: { show: true },
				endAngle: -270,
				clockWise: true,

				data: [
					{
						value: Number(projectSchedule),
						itemStyle: {
							normal: {
								color: "#309CFA"
							}
						}
					},
					{
						value: 100 - Number(projectSchedule),
						itemStyle: {
							normal: {
								shadowBlur: 18,
								shadowColor: "rgba(0,0,0,0)",
								color: "#E7F2FF"
							}
						},
						emphasis: {
							itemStyle: {
								color: "#E7F2FF"
							}
						}
					}
				],
				label: {
					show: false
				}
			},
			{
				type: "gauge",
				startAngle: 90,
				endAngle: -270,
				splitNumber: 2,

				pointer: {
					show: false
				},
				legend: { show: true },
				axisTick: {
					show: true,
					distance: -40,
					length: 37,
					lineStyle: {
						width: 2,
						color: "#E7F2FF",
						borderColor: "#E7F2FF"
					}
				},
				axisLine: {
					show: false,
					lineStyle: {
						width: 40
					}
				},
				splitLine: {
					show: false,
					distance: 0,
					length: 10
				},
				zlevel: 100,
				axisLabel: {
					show: false,
					distance: 50
				},
				title: {
					fontSize: 14
				},
				detail: {
					show: false
				}
			}
		]
	};
	onMounted(() => {
		pieContainerChart = Echarts.init(pieContainer.value);
		pieContainerChart.setOption(option);
	});
</script>

<style lang="scss" scoped>
	.pie-progress {
		background-image: url(/icon/project-info/icon-progress-bg.png);
		background-size: 100% 100%;
	}
</style>
