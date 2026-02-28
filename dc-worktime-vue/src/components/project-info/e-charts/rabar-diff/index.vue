<template>
	<div ref="chartRef" style="width: 100%; height: 280px" class="m-t-10px"></div>
</template>

<script setup lang="ts">
	import { onMounted, ref } from "vue";
	import { useEChartsResize } from "../resize";
	import { object } from "vue-types";

	const { chartRef, initChart } = useEChartsResize();

	const props = defineProps({
		data: object().def()
	});
	const max = Math.max(...[props.data.num5, props.data.num4, props.data.num3, props.data.num2, props.data.num1]) + 30;

	onMounted(() => {
		const option: any = {
			title: {
				text: "雷达图示例",
				show: false
			},
			tooltip: {
				show: false
			},
			legend: {
				data: ["预算分配", "实际开销"],
				show: false
			},
			radar: {
				// shape: 'circle',
				indicator: [
					{ name: "轻松", max: max },
					{ name: "简单", max: max },
					{ name: "正常", max: max },
					{ name: "困难", max: max },
					{ name: "挑战", max: max }
				]
			},
			series: [
				{
					name: "预算 vs 开销",
					type: "radar",
					areaStyle: {},
					data: [
						{
							value: [
								props.data.num5,
								props.data.num4,
								props.data.num3,
								props.data.num2,
								props.data.num1
							],
							name: "预算分配",
							label: {
								show: true,
								formatter: function (params) {
									return params.value;
								}
							}
						}
					]
				}
			]
		};
		initChart(option);
	});
</script>

<style lang="scss" scoped></style>
