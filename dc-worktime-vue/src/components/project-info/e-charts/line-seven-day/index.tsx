import { defineComponent, watch, nextTick } from "vue";
import * as Echarts from "echarts";
import { useEChartsResize } from "../resize";
import { object } from "vue-types";
export default defineComponent({
	name: "lin-seven-day",
	props: {
		data: object().def()
	},
	setup(props) {
		const { chartInstance, chartRef, setChartOption, initChart } = useEChartsResize();
		console.log("33333333333333props", props.data);
		watch(
			() => props.data,
			(val) => {
				const option = {
					title: {
						text: "Stacked Line",
						show: false
					},
					tooltip: {
						trigger: "axis",
						backgroundColor: "#000",
						textStyle: {
							color: "#fff"
						},
						valueFormatter: (value: number | string, dataIndex: number) => `${value}天`
					},
					grid: {
						left: "6%",
						right: "4%",
						bottom: "3%",
						top: "3%",
						containLabel: true
					},
					xAxis: {
						type: "category",
						boundaryGap: false,
						data: props.data.nameList.map((item) => item.split("-").slice(1).join("-")),
						axisTick: {
							show: false,
							lineStyle: {
								type: "dashed"
							}
						},
						axisLine: {
							lineStyle: {
								color: "#9B9B9B"
							}
						},
						axisLabel: {
							interval: 0,
							rotate: 40
						}
					},
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
					series: [
						{
							name: "预计工时",
							type: "line",

							data: props.data.planValueList,
							lineStyle: {
								color: "rgb(77, 119, 255)"
							},
							areaStyle: {
								opacity: 0.3,
								color: new Echarts.graphic.LinearGradient(0, 0, 0, 1, [
									{
										offset: 0,
										color: "rgb(0, 221, 255)"
									},
									{
										offset: 1,
										color: "rgba(77, 119, 255,0)"
									}
								])
							}
						},
						{
							name: "实际工时",
							type: "line",

							data: props.data.workValueList,
							areaStyle: {
								opacity: 0.3,
								color: new Echarts.graphic.LinearGradient(0, 0, 0, 1, [
									{
										offset: 0,
										color: "rgb(0, 221, 255)"
									},
									{
										offset: 1,
										color: "rgba(77, 119, 255,0)"
									}
								])
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
			{ deep: true, immediate: true }
		);
		return () => {
			return <div class="line-seven-day h-208px m-t-20px w-full" ref={chartRef}></div>;
		};
	}
});
