<template>
	<BaseForm v-bind="form" />
</template>

<script setup lang="ts">
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { appInstance } from "@/main";
	import { number } from "vue-types";
	import { getUserList, iterationTree, getPlanAndMilestone } from "@/api/common/login";
	import { saveOrUpdate, info } from "@/api/task/meeting-task";
	import { selectPlanTree } from "@/api/project/plan";
	import { ref, watch } from 'vue';

	const props = defineProps({
		id: number().def(),
		projectId: {
			type: Number,
			default: 0
		},
	});

	const recursionData = (arr) => {
        for (let i = 0; i < arr.length; i++) {
          if (arr[i].type != '3') {
            arr[i].disabled = true;
          }
          if (arr[i].children && arr[i].children.length) {
            recursionData(arr[i].children);
          }
        }
	};

	const { form, registerFormDone } = useDialogStructForm({
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
				span: 24
			},
			{
				label: "关联项目名称",
				value: '',
				type: "ElSelect",
				prop: "project_id",
				span: 24,
				select: {
					type: "ElOption",
					label: "projectName",
					value: "id",
					list: appInstance.projectList,
				},
				attrs: {
					onChange: (e: number[]) => {
						console.log('3333333333e', e)
						iterationTree({
							project_id: e
						}).then((res) => {
							console.log('44444444444444res', res)
							if (form.formItems[3].attrs && form.formItems[3].attrs.options) {
								recursionData(res)
								form.formItems[3].attrs.options = res || [];
							}
						});
						getPlanAndMilestone({
							project_id: e
						}).then((res) => {
							if (form.formItems[4].attrs && form.formItems[4].attrs.options) {
								recursionData(res)
								form.formItems[4].attrs.options = res || [];
							}
						});
					}
				}
			},
			{
				label: "关联项目类型",
				value: 1,
				type: "ElRadioGroup",
				prop: "type",
				span: 8,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "计划",
							value: 1
						},
						{
							label: "里程碑",
							value: 2
						}
					]
				},
				attrs: {
					onChange: (e: number[]) => {
						if(e == 1) {
							form.formItems[3].show = true
							form.formItems[4].show = false
						} else {
							form.formItems[4].show = true
							form.formItems[3].show = false
						}
					}
				}
			},
			{
				label: "关联计划",
				value: '',
				type: "ElCascader",
				prop: "plan_id",
				span: 24,
				select: {
					type: "ElOption",
					list: []
				},
				show: true,
				attrs: {
					options: [],
					props: {
						label: "title",
						value: "id",
						checkStrictly: true,
					},
					showAllLevels: false,
				}
			},
			{
				label: "里程碑",
				value: '',
				type: "ElCascader",
				prop: "milestone_id",
				span: 24,
				select: {
					type: "ElOption",
					list: []
				},
				show: false,
				attrs: {
					options: [],
					props: {
						label: "title",
						value: "id",
						checkStrictly: true,
					},
					showAllLevels: false,
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "meet_date",
				label: "会议日期",
				span: 8,
				attrs: {
					valueFormat: "YYYY-MM-DD",
					format: 'YYYY-MM-DD',
				}
			},
			{
				type: "ElTimePicker",
				value: "",
				prop: "meeting_time",
				label: "会议时间",
				span: 8,
				attrs: {
					valueFormat: "HH:mm:ss",
					format: 'HH:mm:ss',
				}
			},
			{
				type: "ElInput",
				value: "",
				prop: "planing_hours",
				label: "会议时长(分钟)",
				span: 8,
			},
			{
				label: "参会人",
				value: [],
				type: "ElSelect",
				prop: "userIdList",
				span: 24,
				attrs: {
					multiple: true,
					filterable: true,
					clearable: true,
				},
				select: {
					type: "ElOption",
					label: "realName",
					value: "id",
					listApi: async () => {
						const data = await getUserList({
							pageNo: 1,
							pageSize: 100
						});
						return data.records;
					},
					list: []
				}
			},
			{
				label: "会议地点",
				prop: "meet_address",
				value: "",
				type: "ElInput",
				span: 24
			},
		],
		rules: {
			title: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			meet_date: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			meeting_time: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			planing_hours: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			userIdList: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			meet_address: [
				{
					required: true,
					message: "请选择",
					trigger: "blur"
				}
			]
		},
	});
	registerFormDone(async() => {
		form.value.meet_time = form.value.meet_date +' '+ form.value.meeting_time
		console.log('777777777form.value.plan_id', form.value.plan_id)
		form.value.plan_id = form.value.plan_id && Array.isArray(form.value.plan_id) ? form.value.plan_id.pop() : null
		form.value.milestone_id = form.value.milestone_id && Array.isArray(form.value.milestone_id) ? form.value.milestone_id.pop() : null
		console.log('9999999999999999form.value.plan_id', form.value.plan_id, form.value.milestone_id)
		const res = await saveOrUpdate({ ...form.value, id: props.id});
		return res;
	});
	
	// 编辑
	if (props.id) {
		info(props.id).then((res) => {
			Object.keys(form.value).forEach((key) => {
				form.value[key] = res[key];
			});
			form.value.meeting_time = res.meet_time.substring(11, 19)
			form.value.meet_date = res.meet_time.substring(0, 10)
			form.value.userIdList = []
			res.taskMeetUsers.map(val => {
				form.value.userIdList.push(val.userId)
			})
			if(form.value.type == 2) {
				form.formItems[4].show = true
				form.formItems[3].show = false
			}
			if(res.project_id) {
				iterationTree({
					project_id: res.project_id
				}).then((res) => {
					console.log('44444444444444res', res)
					if (form.formItems[3].attrs && form.formItems[3].attrs.options) {
						recursionData(res)
						form.formItems[3].attrs.options = res || [];
					}
				});
				getPlanAndMilestone({
					project_id: res.project_id
				}).then((res) => {
					if (form.formItems[4].attrs && form.formItems[4].attrs.options) {
						recursionData(res)
						form.formItems[4].attrs.options = res || [];
					}
				});
			}
			
		});
	}
</script>
<style lang="scss" scoped>
	.title {
		width: 100%;
		height: 24px;
		line-height: 24px;
		background: linear-gradient(
			90deg,
			rgba(27, 134, 255, 0.1) 0%,
			rgba(27, 134, 255, 0) 100%
		);
		border-radius: 0px 0px 0px 0px;
		border-left: 4px solid #1b86ff;
		font-size: 16px;
		font-weight: bold;
		padding-left: 10px;
	}
</style>
