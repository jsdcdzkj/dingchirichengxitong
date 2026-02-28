<template>
	<div class="m-l-15px flex-1">
		<Title title="近七日偏差曲线"> </Title>
		<LineSevenDay v-if="showRef" :data="data" style="height: 260px" />
	</div>
</template>

<script setup lang="ts">
	import { getWorkHoursDays } from "@/api/task/group";
	import LineSevenDay from "@/components/project-info/e-charts/line-seven-day";
	import { appInstance } from "@/main";
	import { reactive, ref, watch } from "vue";

	const props = defineProps({
		groupId: {
			type: Number,
			default: 0
		}
	});

	const data = reactive({
		planValueList: [],
		nameList: [],
		workValueList: []
	});
	const showRef = ref(false);

	// 封装获取数据的函数
	const fetchData = async () => {
		try {
			const res = await getWorkHoursDays({ groupId: props.groupId });
			Object.keys(data).forEach((key) => {
				Reflect.set(data, key, res[key]);
			});
			showRef.value = true;
		} catch (error) {
			console.error("获取近七日偏差曲线数据失败:", error);
		}
	};

	// 首次加载数据
	fetchData();

	// 监听 props.groupId 变化
	watch(
		() => props.groupId,
		() => {
			fetchData();
		},
		{ immediate: false } // 组件挂载时不立即执行回调
	);
</script>
