import { onBeforeUnmount, ref } from "vue";
import * as echarts from "echarts";
import { appInstance } from "@/main";

export const useEChartsResize = () => {
	const chartRef = ref<HTMLDivElement | null>(null);
	let chartInstance: echarts.ECharts | null = null;

	const resizeChart = () => {
		chartInstance?.resize();
	};

	const resizeChart2 = () => {
		setTimeout(() => {
			chartInstance?.resize();
		}, 300);
	};

	const setChartOption = (option: echarts.EChartsOption) => {
		chartInstance?.setOption(option);
	};

	const initChart = (option: echarts.EChartsOption) => {
		chartInstance = echarts.init(chartRef.value as HTMLDivElement);
		chartInstance.setOption(option);
		window.addEventListener("resize", resizeChart);
		appInstance.mitt.on("changeCollapse", resizeChart2);
	};

	onBeforeUnmount(() => {
		chartInstance?.dispose();
		window.removeEventListener("resize", resizeChart);
		appInstance.mitt.off("changeCollapse", resizeChart2);
	});

	return {
		chartRef,
		setChartOption,
		initChart,
		chartInstance
	};
};
