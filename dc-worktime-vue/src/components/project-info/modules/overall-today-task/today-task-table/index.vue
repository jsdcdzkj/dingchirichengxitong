<template>
	<div class="flex-1">
		<Title title="当日任务统计"> </Title>
		<div class="today-task-table">
			<div class="today-task-table-header">
				<div
					:class="['today-task-table-span', !item.width ? 'flex-1' : '']"
					:style="`width:${item.width}`"
					v-for="item in table.columns"
					:key="item.label"
				>
					{{ item.label }}
				</div>
			</div>
			<div class="today-task-table-content" h="352px" v-if="table.data.length > 0">
				<div class="today-task-table-row" v-for="(item, index) in table.data" :key="index">
					<div
						:class="['today-task-table-span', !table.columns[it - 1].width ? 'flex-1' : '']"
						:style="`width:${table.columns[it - 1].width}`"
						v-for="it in table.columns.length"
						:key="it"
						v-textEllipsis
					>
						<template v-if="table.columns[it - 1].prop == 'highRiskTaskType'">
							<span v-if="item[table.columns[it - 1].prop] == 1">计划任务</span>
							<span v-else-if="item[table.columns[it - 1].prop] == 2">运维任务</span>
							<span v-else-if="item[table.columns[it - 1].prop] == 3">测试bug</span>
							<span v-else>--</span>
						</template>
						<template v-else-if="table.columns[it - 1].prop == 'endTime'">
							{{
								item[table.columns[it - 1].prop] ? item[table.columns[it - 1].prop].slice(0, 10) : "--"
							}}
						</template>

						<template v-else>
							{{ item[table.columns[it - 1].prop] }}
						</template>
					</div>
				</div>
			</div>
			<ElEmpty h="352px" v-else />
		</div>
	</div>
</template>

<script lang="ts" setup>
	import { dayTaskList } from "@/api/project/info";
	import { appInstance } from "@/main";
	import { reactive, ref, watch } from "vue";
	import { number } from "vue-types";

	const props = defineProps({
		type: number().def()
	});

	const list = ref([]);

	const page = reactive({
		pageNo: 1,
		pageSize: 10,
		total: 100
	});

	const table = reactive({
		columns: [
			{ label: "任务标题", prop: "title" },
			{ label: "负责人", prop: "personChargeName", width: "80px" },
			{ label: "任务类型", prop: "highRiskTaskType", width: "120px" },
			{ label: "任务截止日期", prop: "endTime", width: "120px" },
			{ label: "开始日期", prop: "startTime", width: "180px" },
			{ label: "完成日期", prop: "completion_time", width: "180px" },
			{ label: "优先级/严重程度", prop: "priorityName", width: "180px" },
			{ label: "来源项目计划", prop: "sourcePlanName" }
		],
		data: []
	});

	const getList = () => {
		dayTaskList({
			id: appInstance.projectItem.id,
			type: props.type,
			...page
		}).then((res) => {
			table.data = res;
			// console.log(list.value);

			page.total = 1;
		});
	};

	const handleCurrentChange = () => {
		getList();
	};

	getList();
	watch(
		() => props.type,
		() => {
			page.pageNo = 1;
			// 挂起任务
			if (props.type == 4) {
				table.columns[5] = { label: "挂起原因", prop: "reason", width: "180px" };
			}
			// 暂停任务
			else if (props.type == 5) {
				table.columns[5] = { label: "暂停原因", prop: "reason", width: "180px" };
			}
			// 其他任务
			else {
				table.columns[5] = { label: "完成日期", prop: "completion_time", width: "180px" };
			}
			getList();
		}
	);
</script>

<style scoped lang="scss">
	.today-task-table {
		width: 100%;
		height: 100%;
		background-color: #fff;
		border-radius: 10px;
		padding: 10px;
		.today-task-table-header {
			display: flex;
			background: #e6f1ff;
			border-radius: 22px;

			.today-task-table-span {
				height: 20px;
				border-right: 2px solid #fff;
				height: 44px;
				color: #275798;
				line-height: 44px;
				text-align: center;
				font-size: 14px;

				&:last-child {
					border-right: none;
				}
			}
		}

		.today-task-table-content {
			overflow-y: auto;
			overflow-x: hidden;
			.today-task-table-row {
				display: flex;

				border-radius: 22px;
				&:nth-child(odd) {
					background: #fff;
				}
				&:nth-child(even) {
					background: #f4f7ff;
				}

				.today-task-table-span {
					height: 20px;
					border-right: 2px solid #fff;
					height: 44px;
					color: rgba(0, 0, 0, 0.65);
					line-height: 44px;
					text-align: center;
					font-size: 14px;
					font-weight: bold;
					overflow: hidden;
					text-overflow: ellipsis;
					white-space: nowrap;
					width: 0;
					min-width: 0;
					padding: 0 8px;
					&:last-child {
						border-right: none;
					}
				}
			}
		}
	}
</style>
