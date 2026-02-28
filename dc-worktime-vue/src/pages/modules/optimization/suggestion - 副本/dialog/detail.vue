<template><BaseForm v-bind="form"></BaseForm></template>

<script setup lang="ts">
	import { addUpdateInfo } from "@/api/setting/team";
	import { getListAll } from "@/api/setting/user";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { onMounted, ref, toRaw } from "vue";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		groupName: {
			type: String,
			default: ""
		},
		groupType: {
			type: [String, Number], // 根据实际类型修改
			default: ""
		}
	});

	const groupMemberList = ref([]);
	const groupAllList = ref([]);
	const groupSataus = ref(true);

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 90,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "right",
		formItems: [
			{
				label: "组分类",
				value: "",
				type: "ElInput",
				prop: "typeName",
				span: 24,
				attrs: {
					placeholder: "请输入",
					disabled: true
				}
			},
			{
				label: "组名称",
				value: "",
				type: "ElInput",
				prop: "name",
				span: 24,
				attrs: {
					placeholder: "请输入"
				}
			},
			{
				label: "组员",
				value: null,
				type: "ElSelect",
				prop: "sysGroupUserList",
				span: 24,
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					// listApi: async () => {
					// 	const data = await getListAll({});
					// 	return data;
					// },
					list: groupAllList
				},
				attrs: {
					multiple: true,
					clearable: true,
					filterable: true,
					placeholder: "请选择",
					// 监听组员选择变化
					onChange: async (selectedMembers) => {
						groupMemberList.value = groupAllList.value.filter(
							(member) => selectedMembers.includes(member.id)
						);
						if (selectedMembers.length > 0) {
							groupSataus.value = false;
						} else {
							groupSataus.value = true;
						}
					}
				}
			},
			{
				label: "组长",
				value: "",
				type: "ElSelect",
				prop: "leader",
				span: 24,
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					list: groupMemberList
				},
				attrs: {
					clearable: true,
					filterable: true,
					placeholder: "请选择",
					// 初始禁用组长选择框
					disabled: groupSataus
				}
			}
		],
		rules: {
			name: [
				{
					required: true,
					message: "请输入组名称",
					trigger: "blur"
				}
			],
			sysGroupUserList: [
				{
					required: true,
					message: "请选择组员",
					trigger: "change"
				}
			],
			leader: [
				{
					required: true,
					message: "请选择组长",
					trigger: "change"
				}
			]
		}
	});
	// 初始化组员列表
	onMounted(async () => {
		const data = await getListAll({});
		groupAllList.value = data;
		form.value.typeName = props.groupName;
		form.value.type = props.groupType;
	});

	registerFormDone(async () => {
		console.log("form.value", form.value);
		await addUpdateInfo(
			Object.assign({}, toRaw(form.value), {
				id: props.rowInfo.id
			})
		);
		return true;
	});

	if (props.rowInfo.id) {
		Object.keys(form.value).forEach((key) => {
			form.value[key] = props.rowInfo[key];
		});
	}
</script>
