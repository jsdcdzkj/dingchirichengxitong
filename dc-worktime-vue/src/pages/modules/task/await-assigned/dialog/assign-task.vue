<template>
	<div>
		<div class="title m-b-24px">功能信息</div>
		<ElTable border :data="tablelist">
			<ElTableColumn fixed label="序号" width="80" align="center" type="index"> </ElTableColumn>
			<ElTableColumn label="来源开发组任务" show-overflow-tooltip prop="task_type_name" align="center">
				<template #default="scope">
					<ElLink type="primary" @click="handleView(scope.row)">{{ scope.row.task_type_name }}</ElLink>
				</template>
			</ElTableColumn>
			<ElTableColumn label="功能模块" show-overflow-tooltip prop="tree_name" align="center"> </ElTableColumn>
			<ElTableColumn label="功能名称" show-overflow-tooltip prop="feature_name" align="center">
				<template #default="{ row }">
					<!-- 可以点击查看详情 link-->
					<ElLink type="primary" @click="handleDetail(row)">
						{{ row.feature_name }}
					</ElLink>
				</template>
			</ElTableColumn>
			<ElTableColumn label="截止时间" show-overflow-tooltip prop="plan_end_date" align="center"> </ElTableColumn>
			<ElTableColumn label="难易度" width="100px" prop="degree_of_difficulty_name" align="center">
				<template #default="{ row }">
					<span class="degree" v-if="row.degree_of_difficulty == 1"><i class="degree-1"></i>轻松</span>
					<span class="degree" v-if="row.degree_of_difficulty == 2"><i class="degree-2"></i>简单</span>
					<span class="degree" v-if="row.degree_of_difficulty == 3"><i class="degree-3"></i>正常</span>
					<span class="degree" v-if="row.degree_of_difficulty == 4"><i class="degree-4"></i>困难</span>
					<span class="degree" v-if="row.degree_of_difficulty == 5"><i class="degree-5"></i>挑战</span>
				</template>
			</ElTableColumn>
			<ElTableColumn label="开发人员" show-overflow-tooltip prop="historical_developers" align="center">
			</ElTableColumn>
		</ElTable>
		<ElDivider class="m-t-24px m-b-24px" />
		<div class="title m-b-24px">任务分配信息</div>
		<BaseForm v-bind="form" />
		<ElDivider class="m-t-24px m-b-24px" />
		<div class="title m-b-24px">详情</div>
		<BaseForm v-bind="form2" />
		<ElDivider class="m-t-24px m-b-24px" />
		<div class="title m-b-24px">工时</div>
		<BaseForm v-bind="form3" />
	</div>
</template>

<script setup lang="tsx">
	import { ref, computed } from "vue";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { ElDivider, ElTable, ElTableColumn, ElLink } from "element-plus";
	import { saveOrUpdate, getGroupUser } from "@/api/task/await-assigned";
	import { appInstance } from "@/main";
	import { createDrawerAsync } from "@/core/dialog";
	import PlanDetailPage from "@/pages/modules/project/plan-detail/index.vue";
	import DetailPage from "./detailPage.vue";

	const props = defineProps({
		row: {
			type: Array,
			default: () => []
		},
		projectId: {
			type: String,
			default: ""
		}
	});

	const tablelist = computed(() => {
		return props.row;
	});

	const disabledDate = (time: Date) => {
		return time.getTime() > new Date(props.row[0].plan_end_date).getTime();
	};
	console.log("333333333333333props.projectId", props.projectId, appInstance.projectList);
	const { form } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "标题",
				prop: "title",
				value: "",
				type: "ElInput",
				span: 12
			},
			{
				label: "所属项目",
				value: Number(props.projectId),
				type: "ElSelect",
				prop: "project_id",
				span: 12,
				attrs: {
					disabled: true
				},
				select: {
					type: "ElOption",
					label: "projectName",
					value: "id",
					list: appInstance.projectList
				}
			}
			// {
			// 	label: "工作项类型",
			// 	value: "1",
			// 	type: "ElSelect",
			// 	prop: "work_item_type",
			// 	span: 12,
			// 	attrs: {
			// 		disabled: true
			// 	},
			// 	select: {
			// 		type: "ElOption",
			// 		list: appInstance.mapDict.work_item_type
			// 	}
			// }
		],
		rules: {
			title: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			project_id: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
			// work_item_type: [
			// 	{
			// 		required: true,
			// 		message: "请选择",
			// 		trigger: "change"
			// 	}
			// ]
		}
	});

	const { form: form2 } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "描述",
				value: "",
				type: "Editor",
				prop: "description",
				span: 24,
				attrs: {
					style: { height: "150px", width: "100%" },
					contentType: "html"
				}
			},
			{
				label: "负责人",
				value: "",
				type: "ElSelect",
				prop: "assigned_to_user_id",
				span: 12,
				attrs: {
					filterable: true
				},
				select: {
					type: "ElOption",
					label: "userName",
					value: "userId",
					listApi: async () => {
						const data = await getGroupUser({});
						return data;
					},
					list: []
				}
			},
			{
				label: "优先级",
				value: 1,
				type: "ElSelect",
				prop: "priority",
				span: 12,
				select: {
					type: "ElOption",
					list: appInstance.mapDict.priority
				}
			},
			{
				label: "截止日期",
				value: props.row[0].plan_end_date,
				type: "ElDatePicker",
				prop: "end_time",
				span: 12,
				attrs: {
					valueFormat: "YYYY-MM-DD",
					disabledDate: disabledDate
				}
			},
			{
				label: "任务类型",
				value:Number(props.row[0]?.task_type),
				type: "ElSelect",
				prop: "task_attribute",
				span: 12,
				select: {
					type: "ElOption",
					list: appInstance.mapDict.task_attribute
				}
			}
		],
		rules: {
			assigned_to_user_id: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			priority: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			task_attribute: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		}
	});

	const { form: form3, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "难度等级",
				value: 1,
				type: "ElSelect",
				prop: "degree_of_difficulty",
				span: 12,
				select: {
					type: "ElOption",
					list: appInstance.mapDict.degree_of_difficulty
				}
			},
			{
				label: "预估工时（分钟）",
				value: "30",
				type: "ElInputNumber",
				prop: "planing_hours",
				span: 12,
				attrs: {
					min: 0
				}
			}
		],
		rules: {
			degree_of_difficulty: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			planing_hours: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		}
	});
	registerFormDone(async () => {
		const feature_ids: any = [];
		props.row.map((val: any) => {
			feature_ids.push({
				feature_id: val.feature_id,
				plan_task_id: val.plan_task_id,
				plan_id: val.plan_id
			});
		});
		await saveOrUpdate({
			...form.value,
			...form2.value,
			end_time: form2.value.end_time + " 23:59:59",
			...form3.value,
			feature_ids,
			status: 2
		});
		return true;
	});

	const handleView = (row: Object) => {
		createDrawerAsync(
			{ title: row.plan_title, width: "1100px", showNext: false, showConfirm: false },
			{},
			<PlanDetailPage query_detail_status={1} id={row.plan_task_id} />
		);
	};

	const handleDetail = (row: any) => {
		createDrawerAsync(
			{ title: row.feature_name, showNext: false, width: "1056px", showConfirm: false },
			{},
			<DetailPage rowInfo={row} />
		);
	};
</script>
<style lang="scss" scoped>
	:deep(.el-popper) {
		max-width: 500px !important; /* 设置最大宽度 */
		line-height: 1.5 !important; /* 可选：调整行高 */
	}
	.title {
		width: 100%;
		height: 24px;
		line-height: 24px;
		background: linear-gradient(90deg, rgba(27, 134, 255, 0.1) 0%, rgba(27, 134, 255, 0) 100%);
		border-radius: 0px 0px 0px 0px;
		border-left: 4px solid #1b86ff;
		font-size: 16px;
		font-weight: bold;
		padding-left: 10px;
	}
</style>
