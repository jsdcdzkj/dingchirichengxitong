<template>
	<Tabs :tabs="list" v-model:value="current" @change="handleChange" />
	<div class="flex m-t-16px">
		<PieHoursSts :list="pieHoursData" v-if="show" :name="list[current].name" />
		<HoursCountTableTeam :list="listData" v-if="show" :key="tableKey" />
	</div>
</template>

<script setup lang="ts">
	import { getWorkHoursList } from "@/api/task/group";
	import { appInstance } from "@/main";
	import { reactive, ref, watch, onMounted } from "vue";
	const props = defineProps({
		groupId: {
			type: Number,
			default: 0
		},
		startDate: {
			type: String,
			default: ""
		},
		endDate: {
			type: String,
			default: ""
		}
	});
	const pieHoursData = ref<any>([]);
	const listData = ref([] as any[]);
	const show = ref(false);
	const tableKey = ref(0); // 动态 key

	const list = [
		{
			name: "合计工时分布",
			id: 1
		},
		{
			name: "任务工时分布",
			id: 2
		},
		{
			name: "运维工时分布",
			id: 3
		},
		{
			name: "BUG修复任务分布",
			id: 4
		},
		{
			name: "领导交办任务分布",
			id: 5
		}
	];

	const sumDate3 = ref("");
	const startDate = ref("");
	const endDate = ref("");
	const formatDate = (date) => {
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, "0");
		const day = String(date.getDate()).padStart(2, "0");
		return `${year}-${month}-${day}`;
	};

	const keys = [
		"sumWorkHoursTotal",
		"sumWorkHoursTask",
		"sumWorkHoursOperation",
		"sumWorkHoursTestBug",
		"sumWorkHoursLeaders"
	];
	const current = ref(0);
	const handleChange = (index: number) => {
		pieHoursData.value = listData.value
			.filter((item) => Number(item[keys[index]]) > 0)
			.map((item: any) => {
				return {
					name: item.name,
					value: item[keys[index]]
				};
			});
	};

	const fetchData = async () => {
		try {
			const res = await getWorkHoursList({
				groupId: props.groupId,
				completion_start_time: startDate.value,
				completion_end_time: endDate.value
			});
			listData.value = [...res];
			handleChange(0);
			show.value = true;
			tableKey.value++; // 更新 key 值，强制组件重新渲染
		} catch (error) {
			console.error("获取数据失败", error);
		}
	};

	// 处理按月选择
	const handleMonthChange = (month) => {
		if (month) {
			const [year, monthNumber] = month.split("-").map(Number);
			// 计算本月第一天
			const startOfMonth = new Date(year, monthNumber - 1, 1);
			// 计算本月最后一天
			const endOfMonth = new Date(year, monthNumber, 0);

			startDate.value = formatDate(startOfMonth);
			endDate.value = formatDate(endOfMonth);
			fetchData();
		}
	};

	onMounted(() => {
		const currentDate = new Date();
		const year = currentDate.getFullYear();
		// 月份从 0 开始，所以要加 1
		const month = String(currentDate.getMonth() + 1).padStart(2, "0");
		sumDate3.value = `${year}-${month}`;
		// 手动触发数据加载
		handleMonthChange(sumDate3.value);
	});

	watch(
		() => props.groupId,
		() => {
			fetchData();
			current.value = 0;
		},
		{ immediate: false }
	);
	// 监听时间变化
	watch([() => props.startDate, () => props.endDate], ([newStartDate, newEndDate]) => {
		if (newStartDate && newEndDate) {
			startDate.value = newStartDate;
			endDate.value = newEndDate;
			fetchData();
		}
	});
</script>

<style scoped lang="scss"></style>
