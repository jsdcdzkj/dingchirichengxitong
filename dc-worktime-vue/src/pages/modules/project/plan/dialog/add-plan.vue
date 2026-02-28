<template>
	<!-- <div class="flex"> -->
	<BaseForm v-bind="form" />
	<!-- <div style="width: 300px">
			<div style="font-weight: bold; font-size: 16px" class="m-b-10px">关联功能</div>
			<ElTree
				:data="data"
				show-checkbox
				default-expand-all
				ref="treeRef"
				style="max-height: 550px; overflow-y: auto"
				:props="{
					children: 'children',
					label: 'moduleName'
				}"
				node-key="id"
				check-on-click-node
			>
				<template #default="{ node, data }">
					<span class="flex flex-items-center gap-10px">
						<Icon
							:name="data.type == 1 ? `icon-wenjian.png` : `icon-plan.png`"
							:size="{ width: '16px', height: '16px' }"
						></Icon>
						<span>{{ node.label }}</span>
					</span>
				</template>
			</ElTree>
		</div> -->
	<!-- </div> -->
</template>

<script setup lang="ts">
	import { getUserList } from "@/api/common/login";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { ElTree, type TreeInstance } from "element-plus";
	import { Icon } from "@/components/icon/icon";
	import { nextTick, ref } from "vue";
	import dayjs from "dayjs";
	import {
		createPlan,
		editPlan,
		getDeveloper,
		getEntity,
		PlanType,
		selectPlanById,
		selectPlanDetail,
		selectPlanTree
	} from "@/api/project/plan";
	import { appInstance } from "@/main";
	import { number, oneOfType, string } from "vue-types";

	const props = defineProps({
		id: number().def(),
		iterationId: oneOfType([number(), string()]).def(),
		groupId: number().def(),
		limitStartTime: string().def(),
		limitEneTime: string().def()
	});

	const limitStartTime = ref(props.limitStartTime || "");
	const limitEneTime = ref(props.limitEneTime || "");

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		className: "flex-1",
		formItems: [
			{
				label: "标题",
				prop: "plan_title",
				value: "",
				type: "ElInput",
				span: 24,
				attrs: {
					placeholder: "请输入"
				}
			},
			{
				label: "计划负责人",
				value: "",
				type: "ElSelect",
				prop: "person_in_charge",
				span: 8,
				attrs: {
					filterable: true
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						const data = await getDeveloper();
						return data;
					},
					list: []
				}
			},
			{
				label: "所属阶段",
				value: props.groupId,
				type: "ElSelect",
				prop: "parent_id",
				span: 8,
				select: {
					type: "ElOption",
					label: "plan_title",
					value: "id",
					listApi: async () => {
						const data = await selectPlanTree({
							project_id: appInstance.projectItem.id,
							iteration_id: props.iterationId
						});
						return data.filter((item: any) => item.plan_type == PlanType.PLAN_GROUP);
					},
					list: []
				},
				attrs: {
					onChange: (val: any) => {
						selectPlanDetail({ id: val }).then((res) => {
							const { plan_start_date, plan_end_date } = res;
							limitEneTime.value = plan_end_date;
							limitStartTime.value = plan_start_date;
							form.value.plan_start_date = [];
						});
					}
				}
			},
			{
				label: "计划时间",
				value: "",
				type: "ElDatePicker",
				prop: "plan_start_date",
				span: 8,
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始时间",
					endPlaceholder: "结束时间",
					valueFormat: "YYYY-MM-DD",
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
			},

			{
				label: "描述",
				value: "<p><br></p>",
				type: "Editor",
				prop: "plan_desc",
				span: 24,
				attrs: {
					style: { height: "300px", width: "100%" },
					contentType: "html"
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
			plan_desc: [
				{
					required: true,
					message: "请输入",
					trigger: "change",
					validator: (rule, value, callback) => {
						if (value === "<p><br></p>") {
							return callback(new Error("请输入"));
						}
						callback();
					}
				}
			],
			person_in_charge: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			plan_start_date: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			parent_id: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		}
	});
	const data = ref([]);
	const treeRef = ref<TreeInstance>();

	const refreshPlanTree = () => {
		// selectModuleTree({ project_id: appInstance.projectItem.id }).then((res) => {
		// 	// 数据重新组装
		// 	function coverTree(list: any[]) {
		// 		for (let index = 0; index < list.length; index++) {
		// 			const item = list[index];
		// 			Reflect.set(item, "type", 1);
		// 			Reflect.set(item, "id", Math.random().toString(16).slice(0, 12));
		// 			if (item.children.length == 0) {
		// 				if (item.functions.length > 0) {
		// 					item.children = item.functions.map((item: any) => {
		// 						return {
		// 							moduleName: item.feature_name,
		// 							type: 2,
		// 							id: item.id
		// 						};
		// 					});
		// 				} else {
		// 					item.disabled = true;
		// 				}
		// 			} else {
		// 				coverTree(item.children);
		// 			}
		// 		}
		// 	}
		// 	if (res) {
		// 		coverTree(res);
		// 		data.value = res;
		// 	}
		// });
	};

	registerFormDone(async () => {
		const [plan_start_date, plan_end_date] = form.value.plan_start_date;
		const data = {
			project_id: appInstance.projectItem.id,
			plan_title: form.value.plan_title,
			plan_type: PlanType.PLAN,
			plan_start_date,
			plan_end_date,
			parent_id: form.value.parent_id || "",
			plan_desc: form.value.plan_desc,
			person_in_charge: form.value.person_in_charge,
			iteration_id: props.iterationId
			// functionIds: treeRef.value?.getCheckedKeys(true)
		};
		if (!props.id) {
			await createPlan(data);
		} else {
			await editPlan(Object.assign(data, { id: props.id }));
		}
		return form.value;
	});

	if (props.id) {
		selectPlanDetail({ id: props.id }).then((res) => {
			Object.keys(form.value).forEach((key) => {
				form.value[key] = res[key];
			});
			// 时间区间
			if (res.plan_start_date && res.plan_end_date) {
				form.value.plan_start_date = [res.plan_start_date, res.plan_end_date];
			} else {
				form.value.plan_start_date = "";
			}

			nextTick(() => {
				if (res.functionIds) {
					treeRef.value?.setCheckedKeys(res.functionIds, false);
				}
			});
		});
	}
	refreshPlanTree();
</script>
