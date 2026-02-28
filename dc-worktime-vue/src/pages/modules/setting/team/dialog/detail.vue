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

	// 定义组员类型接口
	interface Member {
		id: number;
		realName: string;
	}

	const groupMemberList = ref([]);
	const groupAllList = ref([]);
	const groupSataus = ref(true);

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 70,
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
					placeholder: "请输入组分类",
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
					placeholder: "请输入组名称"
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
					label: "realName",
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
					placeholder: "请选择组员",
					// 监听组员选择变化
					onChange: async (selectedMembers: number[]) => {
						groupMemberList.value = groupAllList.value.filter((member) =>
							selectedMembers.includes(member.id)
						);
						if (selectedMembers.length > 0) {
							groupSataus.value = false;
						} else {
							groupSataus.value = true;
						}
						// 检查组长是否还在组员列表中
						if (!groupMemberList.value.some((member) => member.id === form.value.leader)) {
							form.value.leader = "";
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
					label: "realName",
					value: "id",
					list: groupMemberList
				},
				attrs: {
					clearable: true,
					filterable: true,
					placeholder: "请选择组长",
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

		if (props.rowInfo.id) {
			form.value.name = props.rowInfo.name;
			form.value.leader = props.rowInfo.leader;
			if (props.rowInfo.sysGroupUser && Array.isArray(props.rowInfo.sysGroupUser)) {
				form.value.sysGroupUserList = props.rowInfo.sysGroupUser.map((item) => item.userId);
				groupMemberList.value = groupAllList.value.filter((member) =>
					form.value.sysGroupUserList.includes(member.id)
				);
				// 根据组员是否存在更新组长选择框的禁用状态
				groupSataus.value = form.value.sysGroupUserList.length === 0;
				// 如果组长不在组员列表中，清空组长选择
				if (groupSataus.value || !groupMemberList.value.some((member) => member.id === form.value.leader)) {
					form.value.leader = "";
				}
			} else {
				form.value.sysGroupUserList = [];
				groupMemberList.value = [];
				groupSataus.value = true;
				form.value.leader = "";
			}
		}
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
</script>
