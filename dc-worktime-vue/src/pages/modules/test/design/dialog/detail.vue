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
			<ElCol :span="8">
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
			</ElCol>
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
						界面设计质量是否通过<br />
						1.设计风格符合要求<br />
						2.界面布局合理<br />
						3.交互操作简便易用
					</div>
				</el-descriptions-item>
				<el-descriptions-item label="评价标准" label-align="center">
					<div>
						1.功能实现与信息架构：较为清晰和完整，可用于开发<br />
						(1)功能设计完整性(10)<br />
						是否覆盖所有原型所有功能和页面,是否完成原型标注的需要设计的交互动画，遗漏主要功能或未设计交互动画扣3分/项，次要功能扣1分/项<br />
						(2)数据展示合理性(10)<br />
						表格/图表信息密度合理，字段、信息、数据等展示行距、列宽、操作单元间距等是否合理，偏差1项扣1分<br />
						(3)非正常状态展示完整性(10)<br />
						根据原型文档标注，针对特殊功能提供的默认态、悬停态、点击态、禁用态等基础状态设计，重要功能页面覆盖初始/加载/空数据/异常状态（如无结果提示、错误占位图）图像，缺失1项扣一分
					</div>
					<div style="width: 100%; height: 1px; background-color: rgb(235 238 245); margin: 6px 0"></div>
					<div>
						2.设计效果评分：符合、贴切项目定位和使用单位要求<br />
						(1)调性一致性(10)<br />
						主色、辅助色、字体、图标严格遵循规范和客户趋向要求，严重偏差扣5分/项，非严重偏差扣1分/项<br />
						(2)设计合理性(10)<br />
						信息间隔（间隔、分隔线等）符合要求，标题/正文/辅助文字通过字号或色阶等明确区分，图标风格一致（线性/面性/混合），每偏差一项扣1分<br />
						(3)设计规范性(10)<br />
						相同功能组件（按钮、输入框、弹窗）样式完全一致，栅格系统、元素对齐严格遵循设计标准，每偏差一项扣1分
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
		},
		userList: {
			type: Array,
			default: () => []
		}
	});
	const editStatus = ref(false);
	const form = reactive({
		bug_title: "",
		module_id: "",
		pro_feature_id: "",
		// assigned_to_user_types: [],
		assigned_to_user_type: "3",
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

	const questionList = ref<any[]>([]);
	// const userList = ref<any[]>([]);
	const previewVisible = ref(false);

	const previewShow = () => {
		previewVisible.value = true;
	};
	// 初始化组员列表
	onMounted(async () => {
		const data = await selectDictList({ dictSign: "question_type" });
		questionList.value = data.slice(0, 8);

		// const data2 = await getTypeUserList({ type: "4" });
		// userList.value = data2;

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
			// const targetId = props.rowInfo.assigned_to_user_id;
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
		left: 80px;
		line-height: 26px;
		cursor: pointer;
		&:hover {
			text-decoration: underline;
		}
	}
</style>
