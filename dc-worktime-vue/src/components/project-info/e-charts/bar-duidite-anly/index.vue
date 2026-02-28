<template>
	<div ref="chartRef" style="width: 100%; height: 398px"></div>
</template>

<script lang="ts" setup>
	import { nextTick, onMounted, watch } from "vue";
	import { useEChartsResize } from "../resize";
	import { array } from "vue-types";

	const { initChart, chartRef, setChartOption, chartInstance } = useEChartsResize();
	const props = defineProps({
		xList: array().def([]),
		dataList: array().def([])
	});
	watch(
		() => props.dataList,
		() => {
			const dataArr = {
				xdata: props.xList,
				result: props.dataList
				// result: [
				// 	{ name: "天河", data: [320, 435, 490, 340, 320, 270, 360] },
				// 	{ name: "海珠", data: [150, 220, 210, 310, 140, 180, 288] },
				// 	{ name: "荔湾", data: [250, 120, 240, 280, 240, 180, 188] },
				// 	{ name: "黄埔", data: [180, 420, 370, 250, 210, 180, 228] },
				// 	{ name: "越秀", data: [130, 210, 340, 240, 220, 180, 218] },
				// 	{ name: "白云", data: [210, 260, 230, 220, 260, 180, 388] }
				// ]
			};
			// 头部菱形
			const diamondData = dataArr.result.reduce((pre, cur, index) => {
				pre[index] = cur.data.map((el, id) => el + (pre[index - 1] ? pre[index - 1][id] : 0));
				return pre;
			}, []);

			// color
			const color = [
				[
					{ offset: 0, color: "RGBA(64, 158, 255, 0.8)" },
					{ offset: 0.5, color: "RGBA(64, 158, 255, 0.5)" },
					{ offset: 0.5, color: "RGBA(64, 158, 255, 0.5)" },
					{ offset: 1, color: "RGBA(64, 158, 255, 0.8)" }
				],
				[
					{ offset: 0, color: "RGBA(121, 203, 191, 0.8)" },
					{ offset: 0.5, color: "RGBA(121, 203, 191, 0.5)" },
					{ offset: 0.5, color: "RGBA(121, 203, 191, 0.5)" },
					{ offset: 1, color: "RGBA(121, 203, 191, 0.8)" }
				],
				[
					{ offset: 0, color: "RGBA(115, 203, 151, 0.8)" },
					{ offset: 0.5, color: "RGBA(115, 203, 151, 0.5)" },
					{ offset: 0.5, color: "RGBA(115, 203, 151, 0.5)" },
					{ offset: 1, color: "RGBA(115, 203, 151, 0.8)" }
				],

				[
					{ offset: 0, color: "RGBA(255, 157, 0, 0.8)" },
					{ offset: 0.5, color: "RGBA(255, 157, 0, 0.5)" },
					{ offset: 0.5, color: "RGBA(255, 157, 0,0.5)" },
					{ offset: 1, color: "RGBA(255, 157, 0, 0.8)" }
				],
				[
					{ offset: 0, color: "RGBA(222, 110, 106, 0.8)" },
					{ offset: 0.5, color: "RGBA(222, 110, 106, 0.5)" },
					{ offset: 0.5, color: "RGBA(222, 110, 106, 0.5)" },
					{ offset: 1, color: "RGBA(222, 110, 106, 0.8)" }
				]
			];

			// series
			let series = dataArr.result.reduce((p, c, i, array) => {
				p.push(
					{
						z: i + 1,
						stack: "总量",
						type: "bar",
						name: c.name,
						barGap: "-100%",
						barWidth: 30,
						data: c.data,
						itemStyle: { color: { type: "linear", x: 0, x2: 1, y: 0, y2: 0, colorStops: color[i] } }
					},
					{
						z: i + 10,
						type: "pictorialBar",
						symbolPosition: "end",
						symbol: "diamond",
						symbolOffset: [0, "-50%"],
						symbolSize: [30, 10],
						data: diamondData[i],
						itemStyle: { color: { type: "linear", x: 0, x2: 1, y: 0, y2: 0, colorStops: color[i] } },
						tooltip: { show: false }
					}
				);

				// 是否最后一个了？
				if (p.length === array.length * 2) {
					p.push({
						z: array.length * 2,
						type: "pictorialBar",
						symbolPosition: "start",
						data: dataArr.result[0].data,
						symbol: "diamond",
						symbolOffset: ["0%", "50%"],
						symbolSize: [30, 10],
						itemStyle: { color: { type: "linear", x: 0, x2: 1, y: 0, y2: 0, colorStops: color[0] } },
						tooltip: { show: false }
					});
					return p;
				}

				return p;
			}, []);

			// tooltip
			const tooltip = { trigger: "axis" };

			// legend
			const legend = {
				data: dataArr.result.map((item) => item.name),
				textStyle: { fontSize: 14, color: "#fff" },
				itemWidth: 25,
				itemHeight: 15,
				itemGap: 15,
				bottom: "0%",
				// 禁止点击
				selectedMode: false,
				show: false
			};
			// grid
			const grid = { top: "10%", left: "10%", right: "3%", bottom: "8%" };

			// xAxis
			const xAxis = {
				axisTick: { show: true },
				axisLine: { lineStyle: { color: "#eee" } },
				axisLabel: { textStyle: { fontSize: 12, color: "#999" } },
				data: dataArr.xdata
			};

			// yAxis
			const yAxis = [
				{
					splitLine: { lineStyle: { color: "#eee" } },
					axisLine: { show: false },
					axisLabel: { textStyle: { fontSize: 16, color: "#999" } }
				}
			];

			// 渲染
			const option = { tooltip, xAxis, yAxis, series, grid, legend };

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
