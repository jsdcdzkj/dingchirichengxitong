<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="100px">
		<ElRow>
			<ElCol :span="12">
				<ElFormItem label="父节点" prop="module_id">
					<ElTreeSelect
						v-model="form.module_id"
						placeholder="请选择父节点"
						:data="treeData"
						:props="treeProps"
						filterable
						default-expand-all
						:filter-node-method="filterNode"
						clearable
						:check-strictly="true"
						:render-after-expand="false"
					/>
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="功能名称" prop="feature_name">
					<ElInput v-model="form.feature_name" placeholder="请输入功能名称" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="功能描述" prop="feature_desc">
					<!-- <ElInput
						v-model="form.feature_desc"
						type="textarea"
						maxlength="500"
						:rows="4"
						placeholder="请输入"
						show-word-limit
					/> -->
					<Editor
						ref="featureEditorRef"
						v-model="form.feature_desc"
						contentType="html"
						style="width: 100%; height: calc(50vh - 280px)"
						@ready="onFeatureEditorReady"
					/>
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="难易度" prop="degree_of_difficulty">
					<ElSelect v-model="form.degree_of_difficulty" placeholder="请选择难易度">
						<!-- 利用 prefix 插槽在选中时显示小圆点 -->
						<template #prefix>
							<span
								class="dots selected-dot"
								v-if="form.degree_of_difficulty"
								:style="{ backgroundColor: getColor(form.degree_of_difficulty) }"
							></span>
						</template>
						<template v-for="option in sourceOption" :key="option.dictValue">
							<ElOption :label="option.dictLabel" :value="option.dictValue">
								<template #default>
									<div class="option-item">
										<span
											class="dots option-dot"
											:style="{ backgroundColor: getColor(option.dictValue) }"
										></span>
										<span>{{ option.dictLabel }}</span>
									</div>
								</template>
							</ElOption>
						</template>
					</ElSelect>
					<!-- <ElSelect v-model="form.degree_of_difficulty" placeholder="请选择难易度">
						<template v-for="option in sourceOption" :key="option.dictValue">
							<ElOption :label="option.dictLabel" :value="option.dictValue">
								<template #default>
									<span :style="{ color: getColor(option.dictValue) }">●</span>
									<span>{{ option.dictLabel }}</span>
								</template>
							</ElOption>
						</template>
					</ElSelect> -->
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="难易度描述" prop="degree_of_difficulty_desc">
					<!-- <ElInput
						v-model="form.degree_of_difficulty_desc"
						maxlength="500"
						type="textarea"
						:rows="4"
						placeholder="请输入"
						show-word-limit
					/> -->
					<Editor
						ref="difficultyEditorRef"
						v-model="form.degree_of_difficulty_desc"
						contentType="html"
						style="width: 100%; height: calc(50vh - 280px)"
						@ready="onDifficultyEditorReady"
					/>
				</ElFormItem>
			</ElCol>
			<!-- 附件上传 -->
			<ElCol :span="12">
				<ElFormItem label="功能附件" prop="files">
					<ElUpload
						style="width: 100%"
						v-model="form.files"
						action="/api/project/uploadFile"
						name="multipartFile"
						multiple
						:headers="{ JsdcToken: appInstance.loginToken }"
						:file-list="fileList"
						auto-upload
						:on-update:fileList="
							(e) => {
								form.files = e;
								fileList.value = e;
							}
						"
						:before-upload="beforeUpload"
						:on-success="handleSuccess"
						:on-error="handleError"
						:on-remove="handleRemove"
					>
						<ElButton slot="trigger" :icon="Upload">上传文件</ElButton>
					</ElUpload>
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, onMounted, reactive, toRaw, nextTick } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import {
		ElForm,
		ElFormItem,
		ElInput,
		ElTree,
		ElTreeSelect,
		ElSelect,
		ElOption,
		ElRow,
		ElCol,
		ElButton,
		ElUpload,
		ElMessage
	} from "element-plus";
	import { Upload } from "@element-plus/icons-vue";
	import { createFeature, editFeature, selectFeatureDetail } from "@/api/project/functionList";
	import { selectDictList } from "@/api/setting/dictionary";
	import { appInstance } from "@/main";
	import { processFilesStruct } from "@/api/uploader";

	const props = defineProps({
		projectId: {
			type: Number,
			default: 0
		},
		refreshList: {
			type: Function,
			default: () => () => {}
		},
		moudleIds: {
			type: Number,
			default: 0
		},
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		getTreeData: {
			type: Function,
			default: () => () => {}
		},
		treeData: {
			type: Array,
			default: () => []
		},
		showNext: {
			type: Boolean,
			default: false
		}
	});
	const getColor = (value: number) => {
		switch (value) {
			case 1:
				return "#409eff"; // 轻松级，绿色
			case 2:
				return "#40bfa8"; // 简单级，浅绿色
			case 3:
				return "#67c23a"; // 正常级，黄色
			case 4:
				return "#ff9d00"; // 困难级，橙色
			case 5:
				return "#f56c6c"; // 挑战级，红色
			default:
				return "#000000"; // 默认黑色
		}
	};

	const fileList = ref<any[]>([]);

	const form = reactive({
		module_id: "",
		feature_name: "",
		feature_desc: "",
		degree_of_difficulty: "",
		degree_of_difficulty_desc: "",
		files: []
	});
	const rules = reactive({
		module_id: [{ required: true, message: "请选择父节点", trigger: "blur" }],
		feature_name: [{ required: true, message: "请输入功能名称", trigger: "blur" }],
		feature_desc: [{ required: true, message: "请输入功能描述", trigger: "blur" }],
		degree_of_difficulty: [{ required: true, message: "请选择难易度", trigger: "change" }],
		degree_of_difficulty_desc: [{ required: true, message: "请输入难易度描述", trigger: "blur" }]
	});

	// 难易度
	const sourceOption = ref<any[]>([]);
	const { registerFormDone, formInstance } = useDialogForm();
	const tree = ref<InstanceType<typeof ElTree>>();

	const treeData = ref<any[]>([]);
	const treeProps = {
		children: "children",
		label: "moduleName",
		value: "id",
		disabled: (data: any) => data.hasChildren // 新增disabled判断
	};
	const filterNode = (value: string, data: any) => {
		if (!value) return true;
		// 检查当前节点的 moduleName 是否包含搜索值
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

	const beforeUpload = () => true;

	const handleSuccess = (response, file, list) => {
		fileList.value = list;
		ElMessage.success("上传成功");
	};

	const handleError = () => ElMessage.error("上传失败");

	// 定义 UploadFile 类型，假设结构如下，可根据实际情况调整
	interface UploadFile {
		name: string;
		url: string;
	}

	const handleRemove = (_: UploadFile, files: UploadFile[]) => {
		// 更新表单中的文件列表
		form.files = files;
		// 更新 fileList
		fileList.value = files;
		// 提示用户删除成功
		ElMessage.success("文件删除成功");
	};

	// 初始化组员列表
	onMounted(async () => {
		treeData.value = props.treeData;

		const data = await selectDictList({ dictSign: "degree_of_difficulty" });
		// sourceOption.value只取data中的两个值 id和label
		sourceOption.value = data.map((item: any) => {
			return {
				dictValue: Number(item.dictValue),
				dictLabel: item.dictLabel
			};
		});
		if (props.moudleIds) {
			form.module_id = props.moudleIds;
		}

		if (props.rowInfo.id) {
			selectFeatureDetail({ id: props.rowInfo.id }).then((res) => {
				Object.keys(form).forEach((key) => {
					form[key] = res[key];
				});
				if (res.files) {
					fileList.value = processFilesStruct(res.files);
				}
			});
		}
	});
	const cachedParentId = ref("");
	const featureQuillRef = ref(null);
	const difficultyQuillRef = ref(null);

	const onFeatureEditorReady = (quill) => {
		featureQuillRef.value = quill;
	};

	const onDifficultyEditorReady = (quill) => {
		difficultyQuillRef.value = quill;
	};
	registerFormDone(async () => {
		cachedParentId.value = form.module_id;
		if (props.rowInfo.id) {
			await editFeature(
				Object.assign({}, toRaw(form), {
					project_id: props.projectId,
					files: fileList.value.map((item: any) => item.response.data),
					id: props.rowInfo.id
				})
			);
		} else {
			await createFeature(
				Object.assign({}, toRaw(form), {
					project_id: props.projectId,
					files: fileList.value.map((item: any) => item.response.data)
				})
			);
		}

		props.refreshList();

		// 如果是连续添加模式
		if (props.showNext) {
			// 劫持表单的 resetFields 方法
			if (formInstance.value) {
				const originalReset = formInstance.value.resetFields;
				formInstance.value.resetFields = () => {
					originalReset.call(formInstance.value); // 执行原始重置
					form.module_id = cachedParentId.value; // 强制恢复 module_id
					form.feature_desc = "";
					form.degree_of_difficulty_desc = "";
					form.files = [];
					fileList.value = [];
					// 清空富文本内容

					// 清空功能描述编辑器
					if (featureQuillRef.value) {
						featureQuillRef.value.setContents([]);
					}
					// 清空难易度描述编辑器
					if (difficultyQuillRef.value) {
						difficultyQuillRef.value.setContents([]);
					}
				};
			}
		}
		return true;
	});
</script>
<style scoped lang="scss">
	.dots {
		display: inline-block;
		width: 10px;
		height: 10px;
		border-radius: 50%;
	}
	.selected-dot {
		line-height: 22px;
	}
	.option-dot {
		margin-right: 6px;
	}
	.option-item {
		display: flex;
		height: 100%;
		align-items: center;
	}
</style>
