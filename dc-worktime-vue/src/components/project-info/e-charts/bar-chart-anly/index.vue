<template>
	<div ref="chartRef" style="width: 100%; height: 440px"></div>
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
			const maxData = [10, 100, 100, 100];
			const yList = props.dataList;
			const xList = props.xList;
			const color = ["#3070cf", "#2fd28d", "#c4742d", "#c42d2d"];
			const xData = xList.map((item, index) => {
				return {
					value: item,
					textStyle: {
						color: color[index]
					}
				};
			});
			const dom = 400;
			const barWidth = dom / 10;
			const colors = [
				{
					type: "linear",
					x: 1,
					x2: 1,
					y: 0,
					y2: 1,
					colorStops: [
						{
							offset: 0.8,
							color: "#2CB7F2"
						},
						{
							offset: 0,
							color: "#2689FF"
						}
						/*         {
	       offset: 0.8,   // 透明度
	       color: 'rgba(0,255,252,0.1)'
	   }, {
	       offset: 0.5,
	       color: '#6DD4E5'
	   }, {
	       offset: 0.2,
	       color: '#0EADC9'
	   }, {
	       offset: 0.1,
	       color: '#218bd1'
	   }*/
					]
				}
				// {
				// 	type: "linear",
				// 	x: 0,
				// 	x2: 0,
				// 	y: 0,
				// 	y2: 1,
				// 	colorStops: [
				// 		{
				// 			offset: 0,
				// 			color: "#78cc40"
				// 		},
				// 		{
				// 			offset: 1,
				// 			color: "#2fd28d"
				// 		}
				// 	]
				// },
				// {
				// 	type: "linear",
				// 	x: 0,
				// 	x2: 0,
				// 	y: 0,
				// 	y2: 1,
				// 	colorStops: [
				// 		{
				// 			offset: 0,
				// 			color: "#f5a43a"
				// 		},
				// 		{
				// 			offset: 1,
				// 			color: "#c4742d"
				// 		}
				// 	]
				// },
				// {
				// 	type: "linear",
				// 	x: 0,
				// 	x2: 0,
				// 	y: 0,
				// 	y2: 1,
				// 	colorStops: [
				// 		{
				// 			offset: 0,
				// 			color: "rgba(245,89,58,0.78)"
				// 		},
				// 		{
				// 			offset: 1,
				// 			color: "rgba(196,45,45,1)"
				// 		}
				// 	]
				// },
				// {
				// 	type: "linear",
				// 	x: 0,
				// 	x2: 0,
				// 	y: 0,
				// 	y2: 1,
				// 	colorStops: [
				// 		{
				// 			offset: 0,
				// 			color: "#f5593a"
				// 		},
				// 		{
				// 			offset: 1,
				// 			color: "#c42d2d"
				// 		}
				// 	]
				// }
			];
			const colors1 = [
				{
					type: "linear",
					x: 0,
					x2: 0,
					y: 1,
					y2: 0,
					colorStops: [
						{
							offset: 0.3,
							color: "#2CB7F2"
						},
						{
							offset: 1,
							color: "#2689FF"
						}
					]
				}
				// {
				// 	type: "linear",
				// 	x: 0,
				// 	x2: 0,
				// 	y: 1,
				// 	y2: 0,
				// 	colorStops: [
				// 		{
				// 			offset: 0,
				// 			color: "#c6e755"
				// 		},
				// 		{
				// 			offset: 1,
				// 			color: "#92ee4e"
				// 		}
				// 	]
				// },
				// {
				// 	type: "linear",
				// 	x: 0,
				// 	x2: 0,
				// 	y: 1,
				// 	y2: 0,
				// 	colorStops: [
				// 		{
				// 			offset: 0,
				// 			color: "#ebe962"
				// 		},
				// 		{
				// 			offset: 1,
				// 			color: "#ffb401"
				// 		}
				// 	]
				// },
				// {
				// 	type: "linear",
				// 	x: 0,
				// 	x2: 0,
				// 	y: 1,
				// 	y2: 0,
				// 	colorStops: [
				// 		{
				// 			offset: 0,
				// 			color: "#fa8c56"
				// 		},
				// 		{
				// 			offset: 1,
				// 			color: "#f5593a"
				// 		}
				// 	]
				// }
			];
			const option = {
				//提示框

				/**区域位置*/
				//   grid: {
				//       left: '10%',
				//       right: '10%',
				//       top: 80,
				//       bottom: 40,
				//     },
				/**图例大小*/
				legend: {},
				tooltip: { format: "{b} {c}%" },
				//X轴
				xAxis: {
					// name: "X",
					// nameTextStyle: {
					// 	color: "#FFFFFF",
					// 	padding: [0, 0, 0, 20]
					// },

					data: xData,
					show: true,
					type: "category"
					// axisLine: {
					// 	show: true,
					// 	lineStyle: {
					// 		color: "rgba(255,255,255,0.4)"
					// 	},
					// 	symbol: ["none", "arrow"],
					// 	symbolOffset: [0, 25]
					// },
					// splitLine: {
					// 	show: true,
					// 	lineStyle: {
					// 		color: "rgba(255,255,255,0.2)"
					// 	}
					// },
					// axisLabel: {
					// 	rotate: 0,
					// 	fontSize: 12
					// }
				},
				yAxis: {
					show: true,
					axisLabel: {
						formatter: "{value} 分钟"
					}
					// splitNumber: 4,
					// axisLine: {
					// 	show: true,
					// 	lineStyle: {
					// 		color: "rgba(255,255,255,0.4)"
					// 	}
					// },
					// splitLine: {
					// 	show: true,
					// 	lineStyle: {
					// 		color: "rgba(255,255,255,0.2)"
					// 	}
					// },
					// axisLabel: {
					// 	color: "#FFFFFF"
					// }
				},
				grid: {
					top: "10%",
					left: "5%",
					right: "5%",
					bottom: "1%",
					// right: "50%",
					containLabel: true // gird 区域是否包含坐标轴的刻度标签
				},
				series: [
					{
						type: "bar",
						barWidth: barWidth,
						symbolOffset: ["10%", "50%"],
						itemStyle: {
							normal: {
								color: function (params) {
									return colors[0];
								}
							}
						},
						label: {
							show: true,
							position: "top",
							color: "#ffffff",
							fontSize: 12,
							formatter: function (params) {
								return params.value + "%";
							},
							offset: [0, -20],
							align: "center"
						},
						data: yList
					},

					{
						// 实体柱状图底部
						z: 4,
						type: "pictorialBar",
						data: yList,

						symbolOffset: ["0%", "0%"],
						symbolSize: [barWidth, barWidth * 0.3],
						itemStyle: {
							normal: {
								color: function (params) {
									return "rgba(0,255,252,0.1)";
								}
							}
						}
					},
					{
						// 实体柱状图顶部
						z: 3,
						type: "pictorialBar",
						symbolPosition: "end",
						data: yList,
						symbolOffset: ["0%", "-50%"],
						symbolSize: [barWidth, barWidth * 0.3],
						itemStyle: {
							normal: {
								borderWidth: 0,
								color: function (params) {
									return colors1[0];
								}
							}
						}
					}
					/*  {
	            z: 3,
	           data: maxData,
	           type: 'bar',
	           barMaxWidth: 'auto',
	           barWidth: barWidth + 20,
	           barGap: '-125%',
	           zlevel: -1,
	           itemStyle: {
	               color: 'rgba(0,255,252,0.1)',
	           },
	       },
	       {    // 透明顶部
	           z: 0,
	           type: 'pictorialBar',
	           symbolPosition: 'end',
	           data: maxData,
	           symbolOffset: ['-8%', '-50%'],  // 位置
	           symbolSize: [barWidth + 20, barWidth * 0.3],
	           itemStyle: {
	               color: function (params) {
	                   return 'rgba(0,255,252,0.1)';
	               },
	           },
	       },*/
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
