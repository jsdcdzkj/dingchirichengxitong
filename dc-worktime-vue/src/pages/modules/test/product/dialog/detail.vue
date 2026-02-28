<template>
	<!-- 查看评审标准 -->
	<div class="evaluate-tips" @click="previewShow">查看评审标准</div>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="86px" label-position="top" class="p-r-10px">
		<ElRow :gutter="10">
			<ElCol :span="12">
				<ElFormItem label="问题标题" prop="bug_title">
					<ElInput v-model="form.bug_title" placeholder="请输入标题" />
				</ElFormItem>
			</ElCol>
			<!-- 问题类型 -->
			<ElCol :span="12">
				<ElFormItem label="问题类型">
					<ElSelect v-model="form.question_type" placeholder="请选择" filterable clearable>
						<template v-for="option in questionList" :key="option.dictValue">
							<ElOption :label="option.dictLabel" :value="option.dictValue"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<!-- 责任人 -->
			<ElCol :span="8">
				<ElFormItem label="责任人" prop="assigned_to_user_id">
					<ElSelect v-model="form.assigned_to_user_id" placeholder="请选择" clearable>
						<template v-for="option in props.userList" :key="option.id">
							<ElOption :label="option.username" :value="option.id"></ElOption>
						</template>
					</ElSelect>
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
			<!-- <ElCol :span="8">
				<ElFormItem label="关联目录" prop="module_id">
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
				<ElFormItem label="关联功能点" prop="pro_feature_id">
					<ElSelect v-model="form.pro_feature_id" placeholder="请选择" clearable :disabled="editStatus">
						<template v-for="option in featureOptions" :key="option.value">
							<ElOption :label="option.feature_name" :value="option.id"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol> -->
			<!-- <ElCol :span="8">
				<ElFormItem label="指派至" prop="assigned_to_user_type">
					<ElRadioGroup v-model="form.assigned_to_user_type" :disabled="props.rowInfo.status == 3">
						<ElRadio :value="1">前端</ElRadio>
						<ElRadio :value="2">后端</ElRadio>
					</ElRadioGroup>
				</ElFormItem>
			</ElCol> -->

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
	<!-- 查看评审标准弹窗 -->
	<ElDialog
		title="查看评审标准"
		v-model="previewVisible"
		width="590"
		:close-on-click-modal="false"
		:close-on-press-escape="false"
		:append-to-body="true"
	>
		<div style="padding: 20px">
			<el-descriptions direction="vertical" :column="2" border>
				<el-descriptions-item label="主要评价内容" label-align="center">
					<div style="width: 140px">
						需求分析质量是否通过：<br />
						1.需求分析是否完整<br />
						2.需求拆解是否满足开发条件<br />
					</div>
				</el-descriptions-item>
				<el-descriptions-item label="评价标准" label-align="center">
					<div>
						1.项目需求完整性：较为完整，可用于开发<br />
						(1)需求覆盖度(10)<br />
						是否覆盖所有业务场景和异常流程,遗漏主要场景扣3分/项，次要场景扣1分/项<br />
						(2)功能完善度(10)<br />
						需求拆解到开发可执行单元（如API级/页面级/功能点级），遗漏核心需求扣3分/项，非核心需求扣1分/项<br />
						(3)逻辑一致性(5)<br />
						逻辑冲突扣4分，目标偏离扣5分<br />
						(4)表述清晰度(5)<br />
						重要功能点描述清晰有效，无歧义，少一个扣1分
					</div>
					<div style="width: 100%; height: 1px; background-color: rgb(235 238 245); margin: 6px 0"></div>
					<div>
						2.原型设计评审：业务功能和流程较完善<br />
						(1)业务流程完整性(10)<br />
						核心业务流程闭环，异常分支覆盖，核心功能缺失扣5分/项,非核心功能缺失扣1分/项<br />
						2）界面元素准确性(5)<br />
						控件类型准确（如按钮、表单、多选/单选下拉框），错误1个扣1分<br />
						3）交互逻辑合理性(5)<br />
						用户操作路径≤4步完成高频功能，无冗余跳转,路径复杂扣2分，逻辑死循环扣5分
					</div>
					<div style="width: 100%; height: 1px; background-color: rgb(235 238 245); margin: 6px 0"></div>
					<div>
						3.良好的用户体验：原型交互设计友好，操作流畅，无明显的设计不合理之处<br />
						(1)信息层级清晰度(5)<br />
						主次信息区分明显（如标题放大、辅助文案弱化）,每出现一个问题扣1分<br />
						(2)反馈机制准确性(5)<br />
						重要/核心操作结果提示明确（成功/失败/等待），无提示扣1分/项
					</div>
				</el-descriptions-item>
			</el-descriptions>
		</div>
		<!-- <div slot="footer" class="dialog-footer">
			<ElButton type="primary" @click="previewVisible = false">关闭</ElButton>
		</div> -->
	</ElDialog>
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
		ElMessage,
		ElDialog,
		ElDescriptions,
		ElDescriptionsItem,
		ElButton
	} from "element-plus";
	import { addUpdateInfo, getTypeUserList } from "@/api/test/issues";
	import { selectDictList } from "@/api/setting/dictionary";
	import { getListAll } from "@/api/setting/user";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";

	const props = defineProps({
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
		},
		userList: {
			type: Array,
			default: () => []
		}
	});
	const editStatus = ref(false);
	const form = reactive({
		bug_title: "",
		// module_id: "",
		// pro_feature_id: "",
		// assigned_to_user_types: [],
		assigned_to_user_type: "4",
		end_time: "",
		severity: "",
		source: "",
		description: "",
		status: 1,
		assigned_to_user_id: "",
		question_type: ""
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
		],
		assigned_to_user_id: [
			{
				required: true,
				message: "请选择责任人",
				trigger: "blur"
			}
		],
		description: [
			{
				required: true,
				message: "请输入问题描述",
				trigger: "blur"
			}
		]
	});

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

	const questionList = ref<any[]>([]);
	const userList = ref<any[]>([]);
	const previewVisible = ref(false);

	const previewShow = () => {
		previewVisible.value = true;
	};
	// 初始化组员列表
	onMounted(async () => {
		const data = await selectDictList({ dictSign: "question_type" });
		questionList.value = data.slice(8);

		const data2 = await getTypeUserList({ type: "3" });
		userList.value = data2;

		if (props.rowInfo && props.rowInfo.id) {
			await handleModuleChange(props.rowInfo.module_id);
			const formKeys = Object.keys(form);
			formKeys.forEach((key) => {
				if (props.rowInfo.hasOwnProperty(key)) {
					form[key] = props.rowInfo[key];
				}
			});
			form.severity = Number(props.rowInfo.severity);
			// const targetId = String(props.rowInfo.assigned_to_user_id);
			// form.assigned_to_user_id = targetId;
			editStatus.value = true;
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
<style>
	.evaluate-tips {
		position: absolute;
		top: 20px;
		font-size: 14px;
		color: var(--el-color-primary);
		left: 90px;
		line-height: 26px;
		cursor: pointer;
		&:hover {
			text-decoration: underline;
		}
	}
</style>
