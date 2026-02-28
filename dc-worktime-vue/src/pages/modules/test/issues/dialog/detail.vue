<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="86px" label-position="top" class="p-r-10px">
		<ElRow :gutter="10">
			<ElCol :span="24">
				<ElFormItem label="标题" prop="bug_title">
					<ElInput v-model="form.bug_title" placeholder="请输入标题" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="所属模块" prop="module_id">
					<ElTreeSelect
						v-model="form.module_id"
						placeholder="请选择父级菜单"
						:data="treeData"
						:props="treeProps"
						default-expand-all
						filterable
						:filter-node-method="filterNode"
						clearable
						:check-strictly="true"
						:render-after-expand="false"
						@change="handleModuleChange"
						:disabled="editStatus"
					/>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="功能名称" prop="pro_feature_id">
					<ElSelect v-model="form.pro_feature_id" placeholder="请选择" clearable :disabled="editStatus">
						<template v-for="option in featureOptions" :key="option.value">
							<ElOption :label="option.feature_name" :value="option.id"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="指派至" prop="assigned_to_user_type">
					<!-- <ElCheckboxGroup v-model="form.assigned_to_user_types" :disabled="editStatus">
						<ElCheckbox :value="option.value" v-for="option in userOptions" :key="option.value">
							{{ option.label }}
						</ElCheckbox>
					</ElCheckboxGroup> -->
					<ElRadioGroup v-model="form.assigned_to_user_type" :disabled="props.rowInfo.status == 3">
						<ElRadio :value="1">前端</ElRadio>
						<ElRadio :value="2">后端</ElRadio>
					</ElRadioGroup>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="截止时间" prop="end_time">
					<ElDatePicker
						v-model="form.end_time"
						type="date"
						format="YYYY-MM-DD"
						value-format="YYYY-MM-DD"
						:disabled-date="disabledDate"
						placeholder="选择日期"
						style="width: 100%"
					/>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="严重程度" prop="severity">
					<ElSelect v-model="form.severity" placeholder="请选择">
						<template v-for="option in severityList" :key="option.value">
							<ElOption :label="option.label" :value="option.value"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="来源" prop="source">
					<ElSelect v-model="form.source" placeholder="请选择">
						<template v-for="option in sourceOption" :key="option.value">
							<ElOption :label="option.label" :value="option.value"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="问题描述" prop="description">
					<Editor
						v-model="form.description"
						contentType="html"
						style="width: 100%; height: calc(100vh - 540px)"
					/>
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, onMounted, reactive, toRaw, computed, nextTick } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import {
		ElForm,
		ElFormItem,
		ElInput,
		ElTree,
		ElTreeSelect,
		ElCheckbox,
		ElCheckboxGroup,
		ElRadioGroup,
		ElRadio,
		ElDatePicker,
		ElSelect,
		ElOption,
		ElRow,
		ElCol,
		ElMessage
	} from "element-plus";
	import { addUpdateInfo } from "@/api/test/issues";
	import { getListAll } from "@/api/setting/user";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";

	const props = defineProps({
		moudleList: {
			type: [Array, Object],
			default: () => []
		},
		projectId: {
			type: Number,
			default: 0
		},
		refreshList: {
			type: Function,
			default: () => () => {}
		},
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});
	const editStatus = ref(false);
	const form = reactive({
		bug_title: "",
		module_id: "",
		pro_feature_id: "",
		// assigned_to_user_types: [],
		assigned_to_user_type: "",
		end_time: "",
		severity: "",
		source: "",
		description: "",
		status: 1
	});
	const rules = reactive({
		bug_title: [
			{
				required: true,
				message: "请输入标题",
				trigger: "blur"
			}
		],
		module_id: [
			{
				required: true,
				message: "请选择所属模块",
				trigger: "blur"
			}
		],
		pro_feature_id: [
			{
				required: true,
				message: "请选择功能名称",
				trigger: "blur"
			}
		],
		assigned_to_user_type: [
			{
				required: true,
				message: "请选择指派至",
				trigger: "blur"
			}
		],
		end_time: [
			{
				required: true,
				message: "请选择截止时间",
				trigger: "blur"
			}
		],
		severity: [
			{
				required: true,
				message: "请选择严重程度",
				trigger: "blur"
			}
		],
		source: [
			{
				required: true,
				message: "请选择来源",
				trigger: "blur"
			}
		]
	});

	const moudleList = ref(props.moudleList);
	const featureOptions = ref<any[]>([]);
	// 1：前端 2：后端 3：部署 4：测试
	const userOptions = ref<any[]>([
		{
			label: "前端",
			value: 1
		},
		{
			label: "后端",
			value: 2
		}
	]);
	// severityList 1:轻微bug 2：一般bug 3：重大bug
	const severityList = ref<any[]>([
		{
			label: "轻微",
			value: 1
		},
		{
			label: "一般",
			value: 2
		},
		{
			label: "严重",
			value: 3
		}
	]);

	// bug来源 1.任务BUG 2.评审BUG sourceOption
	const sourceOption = ref<any[]>([
		{
			label: "任务",
			value: 1
		},
		{
			label: "评审",
			value: 2
		},
		{
			label: "联调",
			value: 3
		}
	]);
	// 禁用当前日期之前的日期
	const disabledDate = (date: Date) => {
		return date < new Date(new Date().setHours(0, 0, 0, 0));
	};

	const { registerFormDone, formInstance } = useDialogForm();
	const tree = ref<InstanceType<typeof ElTree>>();

	const treeData = ref<any[]>([]);
	const treeProps = {
		children: "children",
		label: "moduleName",
		value: "id"
	};
	const filterNode = (value: string, data: any) => {
		if (!value) return true;
		// 检查当前节点的 title 是否包含搜索值
		if (data.moduleName && data.moduleName.includes(value)) {
			return true;
		}
		// 递归检查子节点
		if (data.children && data.children.length > 0) {
			for (let i = 0; i < data.children.length; i++) {
				if (filterNode(value, data.children[i])) {
					return true;
				}
			}
		}
		return false;
	};

	const defaultTime = computed(() => {
		const time = new Date();
		time.setHours(23, 59, 59, 0); // 设置时分秒为 23:59:59
		return [time];
	});
	// 初始化组员列表
	onMounted(async () => {
		treeData.value = moudleList.value;
		if (props.rowInfo && props.rowInfo.id) {
			await handleModuleChange(props.rowInfo.module_id);
			const formKeys = Object.keys(form);
			formKeys.forEach((key) => {
				if (props.rowInfo.hasOwnProperty(key)) {
					form[key] = props.rowInfo[key];
				}
			});
			// form.assigned_to_user_types = [props.rowInfo.assigned_to_user_type];
			form.severity = Number(props.rowInfo.severity);
			editStatus.value = true;
			console.log(editStatus.value);
		}
	});
	// 处理模块选择变化的事件
	const handleModuleChange = (value: any) => {
		// 清空 pro_feature_id
		form.pro_feature_id = "";
		// 查找选中的节点
		const findNode = (nodes: any[]) => {
			for (const node of nodes) {
				if (node.id === value) {
					return node;
				}
				if (node.children) {
					const result = findNode(node.children);
					if (result) {
						return result;
					}
				}
			}
			return null;
		};
		const selectedNode = findNode(treeData.value);
		if (selectedNode && selectedNode.functions) {
			// 将选中节点的 functions 赋值给 featureOptions
			featureOptions.value = selectedNode.functions;
		} else {
			// 如果没有找到 functions，清空 featureOptions
			featureOptions.value = [];
		}
	};
	registerFormDone(async () => {
		const response = await addUpdateInfo(
			Object.assign({}, toRaw(form), props.rowInfo ? { id: props.rowInfo.id } : {}, {
				pro_project_id: props.projectId
			})
		);
		if (response && response.code === 500) {
			// 显示错误信息
			ElMessage.error(response.msg || "请求发生错误");
			return false;
		} else {
			// ElMessage.success("操作成功");
			props.refreshList();
			return true;
		}
	});
</script>
