<template>
	<div class="flex">
		<div>
			<BaseForm v-bind="form" />
			<ElDivider class="m-t-10px m-b-10px" />
			<div class="title m-t-10px m-b-10px">详情</div>
			<BaseForm v-bind="form2" />
			<ElDivider class="m-t-10px m-b-10px" />
			<div class="title m-t-10px m-b-10px">关联内容</div>
			<!-- <div class="m-t-20px m-b-10px">关联计划或计划组</div> -->
			<BaseForm v-bind="form4" />
			<!-- <div class="select-item m-b-20px">
				{{ props.row.plan_title }}
			</div> -->
			<ElDivider class="m-t-10px m-b-10px" v-if="showWorkHour" />
			<div class="title m-t-10px m-b-10px" v-if="showWorkHour">工时</div>
			<BaseForm v-bind="form3" v-if="showWorkHour" />
		</div>
		<div style="width: 500px; flex-shrink: 0" class="flex flex-col">
			<div style="font-weight: bold; font-size: 16px" class="m-b-10px">
				关联功能
				<ElInput v-model:model-value="filterText" placeholder="请输入" style="margin-top: 10px" />
			</div>
			<div style="flex: 1; overflow-y: hidden; height: 800px">
				<ElTree
					:data="data"
					show-checkbox
					default-expand-all
					ref="treeRef"
					style="height: 100%; overflow-y: auto; height: 800px"
					:props="{
						children: 'children',
						label: 'moduleName'
					}"
					:filter-node-method="filterNode"
					node-key="id"
					check-on-click-node
				>
					<template #default="{ node, data }">
						<div class="flex justify-between w-full">
							<span class="flex flex-items-center gap-10px">
								<Icon
									:name="data.type == 1 ? `icon-wenjian.png` : `icon-plan.png`"
									:size="{ width: '16px', height: '16px' }"
								></Icon>
								<span class="tree-label" :style="{ maxWidth: `${190 - node.level * 18}px` }">{{
									node.label
								}}</span>
							</span>
							<div style="padding-right: 10px" class="flex gap-4px items-center" v-if="data.function">
								<ElTag @click.stop="handleOptSelect(data)" type="primary">详情</ElTag>
								<Icon name="code-box-fill.png" :size="{ width: '16px', height: '16px' }" />
								<span>{{ data.devNames || "--" }}</span>
							</div>
						</div>
					</template>
				</ElTree>
			</div>
		</div>
	</div>
</template>

<script setup lang="tsx">
	import { createTask, editTask, getDeveloper, selectModuleTree } from "@/api/project/plan";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { appInstance } from "@/main";
	import { ElDivider, ElTree, ElInput, type TreeInstance, ElTag } from "element-plus";
	import { number, object, oneOfType, string, bool } from "vue-types";
	import { selectPlanDetail, selectPlanTree } from "@/api/project/plan";
	import { nextTick, ref, watch } from "vue";
	import { Icon } from "@/components/icon/icon";
	import dayjs from "dayjs";
	import { createDrawerAsync } from "@/core/dialog";
	import DetailPage from "../../function-list/dialog/detailPage.vue";
	const props = defineProps({
		row: object().def(),
		id: number().def(),
		iterationId: oneOfType([number(), string()]).def(),
		groupId: number().def(),
		limitStartTime: string().def(),
		limitEneTime: string().def(),
		isCopy: bool().def(false)
	});

	const limitStartTime = ref(props.limitStartTime || "");
	const limitEneTime = ref(props.limitEneTime || "");
	interface Tree {
		[key: string]: any;
	}
	const data = ref([]);
	const filterText = ref("");
	const treeRef = ref<TreeInstance>();
	const showWorkHour = ref(false);
	const hasTaskFunctionIds = ref([]);

	watch(filterText, (val) => {
		treeRef.value!.filter(val);
	});

	const handleOptSelect = (e: any) => {
		createDrawerAsync(
			{ title: "详情", showNext: false, width: "1056px", showConfirm: false },
			{},
			<DetailPage rowId={e.id} />
		);
	};

	const filterNode = (value: string, data: Tree) => {
		if (!value) return true;

		return data.moduleName.includes(value);
	};

	const refreshPlanTree = () => {
		selectModuleTree({ project_id: appInstance.projectItem.id, iteration_id: props.iterationId }).then((res) => {
			// 数据重新组装
			function coverTree(list: any[]) {
				for (let index = 0; index < list.length; index++) {
					const item = list[index];
					Reflect.set(item, "type", 1);
					Reflect.set(item, "id", Math.random().toString(16).slice(0, 12));
					if (item.children.length == 0) {
						if (item.functions.length > 0) {
							item.children = item.functions.map((functionItem: any) => {
								return {
									moduleName: functionItem.feature_name,
									type: 2,
									id: functionItem.id,
									function: true,
									devNames: functionItem.devNames,
									disabled: hasTaskFunctionIds.value.includes(functionItem.id)
								};
							});
						} else {
							item.disabled = true;
						}
					} else {
						coverTree(item.children);
					}
				}
			}
			if (res) {
				coverTree(res);
				data.value = res;
			}
		});
	};

	const { form } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "标题",
				prop: "plan_title",
				value: "",
				type: "ElInput",
				span: 16,
				attrs: {
					placeholder: "请输入"
				}
			},
			// {
			// 	label: "所属项目",
			// 	value: appInstance.projectItem.id,
			// 	type: "ElSelect",
			// 	prop: "project_id",
			// 	span: 8,
			// 	select: {
			// 		type: "ElOption",
			// 		label: "projectName",
			// 		value: "id",
			// 		list: appInstance.projectList
			// 	}
			// },
			{
				label: "任务类型",
				value: "",
				type: "ElSelect",
				prop: "task_type",
				span: 8,
				select: {
					type: "ElOption",
					label: "label",
					value: "value",
					// 任务类型: 1.售前 2.前端 3.后端 4 UI 5 测试 6 产品"
					list: appInstance.mapDict.task_attribute
				},
				attrs: {
					onChange: (val: any) => {
						showWorkHour.value = val == 5 || val == 3 || val == 6;
					},
					disabled: !props.isCopy && props.id
				}
			}
		],
		rules: {
			plan_title: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			// project_id: [
			// 	{
			// 		required: true,
			// 		message: "请选择",
			// 		trigger: "change"
			// 	}
			// ],
			task_type: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
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
				value: "<p><br></p>",
				type: "Editor",
				prop: "plan_desc",
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
				prop: "person_in_charge",
				span: 8,
				select: {
					type: "ElOption",
					label: "username",

					value: "id",
					listApi: async () => {
						const data = await getDeveloper();
						return data;
					},
					list: []
				},
				attrs: {
					filterable: true
				}
			},
			{
				label: "优先级",
				value: 1,
				type: "ElSelect",
				prop: "priority",
				span: 8,
				select: {
					type: "ElOption",
					list: appInstance.mapDict.priority
				}
			},
			{
				label: "难易程度",
				value: 1,
				type: "ElSelect",
				prop: "degree_of_difficulty",
				span: 8,
				select: {
					type: "ElOption",
					list: appInstance.mapDict.degree_of_difficulty
				}
			},
			{
				label: "截止时间",
				value: "",
				type: "ElDatePicker",
				prop: "plan_end_date",
				span: 8,
				attrs: {
					valueFormat: "YYYY-MM-DD",
					placeholder: "请选择",
					disabledDate: (time: Date) => {
						const timeT = new Date(dayjs(time).format("YYYY-MM-DD")).getTime();
						// const timeT = time.getTime();
						if (limitStartTime.value && limitEneTime.value) {
							const lStart = new Date(limitStartTime.value).getTime();
							const lEnd = new Date(limitEneTime.value).getTime();
							return timeT < lStart || timeT > lEnd;
						} else if (limitStartTime.value) {
							const lStart = new Date(limitStartTime.value).getTime();
							return timeT < lStart;
						} else if (limitEneTime.value) {
							const lEnd = new Date(limitEneTime.value).getTime();
							return timeT > lEnd;
						}
						return false;
					}
				}
			}
			// {
			// 	label: "任务属性",
			// 	value: "",
			// 	type: "ElSelect",
			// 	prop: "task_attribute",
			// 	span: 8,
			// 	select: {
			// 		type: "ElOption",
			// 		list: appInstance.mapDict.task_attribute
			// 	}
			// }
		],
		rules: {
			plan_desc: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			person_in_charge: [
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
			plan_end_date: [
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
				label: "预估工时（分钟）",
				value: 30,
				type: "ElInputNumber",
				prop: "work_hours",
				span: 12,
				attrs: {
					min: 0
				}
			}
		],
		rules: {
			work_hours: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			]
		}
	});

	const { form: form4 } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "关联计划",
				value: props.row.id,
				type: "ElCascader",
				prop: "parent_id",
				span: 12,
				select: {
					type: "ElOption",
					listApi: async () => {
						const data = await selectPlanTree({
							project_id: appInstance.projectItem.id,
							iteration_id: props.iterationId
						});
						return data;
					}
				},
				attrs: {
					options: [],
					props: {
						label: "plan_title",
						value: "id",
						checkStrictly: true
					},

					onChange: (val: any) => {
						selectPlanDetail({ id: val.slice(-1)[0] }).then((res) => {
							const { plan_start_date, plan_end_date } = res;
							limitEneTime.value = plan_end_date;
							limitStartTime.value = plan_start_date;
							form2.value.plan_end_date = "";
						});
					}
				}
			}
		],
		rules: {
			planing_hours: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			]
		}
	});

	registerFormDone(async () => {
		const data = Object.assign(
			{},
			{
				...form.value,
				...form2.value,
				...form3.value,
				parent_id:
					form4.value.parent_id instanceof Array ? form4.value.parent_id.slice(-1)[0] : form4.value.parent_id,
				// parent_id: appInstance.projectItem.id,
				project_id: appInstance.projectItem.id,
				functionIds: treeRef.value?.getCheckedKeys(true),
				iteration_id: props.iterationId,
				work_hours: showWorkHour.value ? form3.value.work_hours : 0
			},
			props.id && !props.isCopy ? { id: props.id } : {}
		);
		props.id && !props.isCopy ? await editTask(data) : await createTask(data);
		return form.value;
	});

	if (props.id) {
		selectPlanDetail({ id: props.id, query_detail_status: 2 }).then((res) => {
			// form.value = mergeProps(form.value, res);
			// 赋值
			Object.keys(form.value).forEach((key) => {
				form.value[key] = res[key];
			});
			Object.keys(form2.value).forEach((key) => {
				form2.value[key] = res[key];
			});
			// Object.keys(form3.value).forEach((key) => {
			// 	form3.value[key] = res[key];
			// });
			Object.keys(form4.value).forEach((key) => {
				form4.value[key] = res[key];
			});
			// 权重
			form2.value.priority = res.priority + "";
			// 难度
			form2.value.degree_of_difficulty = res.degree_of_difficulty + "";
			form3.value.work_hours = Number(res.work_hours);
			form2.value.degree_of_difficulty = Number(res.degree_of_difficulty);
			form2.value.priority = Number(res.priority);
			form.value.task_type = res.task_type;
			hasTaskFunctionIds.value = res.hasTaskFunctionIds || [];
			refreshPlanTree();
			// 时间
			// const { plan_start_date, plan_end_date } = res;
			// limitEneTime.value = plan_end_date;
			// limitStartTime.value = plan_start_date;
			showWorkHour.value = res.task_type == 5 || res.task_type == 3 || res.task_type == 6;
			// 关联功能
			nextTick(() => {
				if (res.functionIds) {
					treeRef.value?.setCheckedKeys(res.functionIds, false);
				}
			});
		});
	} else {
		refreshPlanTree();
	}
</script>
<style lang="scss" scoped>
	.tree-label {
		display: inline-block;
		max-width: 190px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
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

	.select-item {
		height: 40px;
		display: flex;
		align-items: center;
		gap: 10px;
		padding: 0 10px;
		background: #f8f8f8;
		border-radius: 4px 4px 4px 4px;
	}
	.drawer-container-main {
		overflow-y: hidden;
	}
</style>
