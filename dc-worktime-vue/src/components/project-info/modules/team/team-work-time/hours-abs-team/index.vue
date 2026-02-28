<template>
	<div class="flex-shrink-0" w="406px">
		<Title title="工时对比表">
			<template #right>
				<ElDatePicker
					v-model="sumDate3"
					type="month"
					format="YYYY-MM"
					value-format="YYYY-MM"
					placeholder="选择月"
					style="width: 140px; margin-left: 8px"
					@change="handleMonthChange"
				/>
			</template>
		</Title>
		<div class="abs-table m-t-20px">
			<div class="abs-table__head">
				<div class="abs-table__head__item relative" w="125px">
					<img
						w="49px"
						h="49px"
						class="absolute top--10px left-15px"
						src="/icon/project-info/icon-abs-money.png"
						alt=""
						srcset=""
					/>
				</div>
				<div class="abs-table__head__item" w="76px">
					<span class="abs-table__head__item__name">预估工时</span>
					<span class="abs-table__head__item__unit">(人天)</span>
				</div>
				<div class="abs-table__head__item" w="76px">
					<span class="abs-table__head__item__name">实际工时</span>
					<span class="abs-table__head__item__unit">(人天)</span>
				</div>
				<div class="abs-table__head__item" w="110px">
					<span class="abs-table__head__item__name">项目开发成本</span>
					<span class="abs-table__head__item__unit">(元)</span>
				</div>
			</div>
			<div class="abs-table__body">
				<div class="abs-table__body__item" v-for="item in list" :key="item.name">
					<div class="abs-table__body__item__cell cell-1" w="125px">
						{{ item.name }}
						<!-- <ElIcon style="color: #c0d3ea"><QuestionFilled /></ElIcon> -->
					</div>
					<div class="abs-table__body__item__cell item-color" w="76px">{{ item.planing_hours }}</div>
					<div class="abs-table__body__item__cell item-color" w="76px">{{ item.working_hours }}</div>
					<div class="abs-table__body__item__cell item-color" w="110px">{{ item.project_cost }}</div>
				</div>
			</div>
		</div>
	</div>
</template>
<script setup lang="ts">
	import { getWorkHoursContrast } from "@/api/task/group";
	import { QuestionFilled } from "@element-plus/icons-vue";
	import { ref, onMounted, watch, defineEmits } from "vue";
	const props = defineProps({
		projectId: {
			type: Number,
			default: 0
		},
		groupId: {
			type: Number,
			default: 0
		}
	});
	const list = ref<any>([]);

	const sumDate3 = ref("");
	const startDate = ref("");
	const endDate = ref("");
	const formatDate = (date) => {
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, "0");
		const day = String(date.getDate()).padStart(2, "0");
		return `${year}-${month}-${day}`;
	};

	const emit = defineEmits(["date-changed"]);

	// 监听时间变化并触发事件
	watch([startDate, endDate], ([newStartDate, newEndDate]) => {
		emit("date-changed", newStartDate, newEndDate);
	});

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
			initGetList();
		}
	};

	const initGetList = () => {
		list.value = [];
		getWorkHoursContrast({
			// project_id: props.projectId,
			groupId: props.groupId,
			completion_start_time: startDate.value,
			completion_end_time: endDate.value
		}).then((res) => {
			Object.keys(res).forEach((key) => {
				list.value.push(res[key]);
			});
		});
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
			initGetList(sumDate3.value);
		},
		{ immediate: false } // 组件挂载时不立即执行回调
	);
</script>
<style lang="scss" scoped>
	.abs-table {
		height: 260px;
		&__head {
			display: flex;
			gap: 6px;

			&__item {
				background: #e6f2ff;
				flex-shrink: 0;
				text-align: center;
				height: 48px;
				border-radius: 8px 8px 8px 8px;
				font-size: 14px;
				color: #275798;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				&__name {
					font-weight: 600;
				}
				&__unit {
					font-weight: 400;
					font-size: 12px;
					color: #275798;
				}
			}
		}
		&__body {
			display: flex;
			flex-direction: column;
			gap: 6px;
			margin-top: 6px;
			&__item {
				display: flex;
				gap: 6px;
				&__cell {
					background: #e6f2ff;
					height: 34px;
					border-radius: 8px 8px 8px 8px;
					font-size: 14px;
					color: rgba(0, 0, 0, 0.85);
					font-weight: bold;
					text-align: center;
					line-height: 34px;
					&.cell-1 {
						color: #275798;
						display: flex;
						align-items: center;
						justify-content: center;
						gap: 4px;
					}
					&.item-color {
						background: #f4f7ff;
					}
				}
			}
		}
	}
</style>
