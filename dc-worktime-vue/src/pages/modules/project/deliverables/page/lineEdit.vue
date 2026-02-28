<template>
	<!-- ElTable -->
	<ElForm ref="formInstance" :model="form" :rules="rules">
		<ElTable border :data="tableData" stripe>
			<!-- 资料名称 -->
			<ElTableColumn label="资料名称" min-width="160" align="center" v-if="props.rowType == 1">
				<template #default="scope">
					<ElFormItem :prop="`tableData[${scope.$index}].dataFileName`" :rules="rules.dataFileName">
						<ElInput v-model="scope.row.dataFileName" placeholder="请输入资料名称" />
					</ElFormItem>
				</template>
			</ElTableColumn>
			<!-- 类型 -->
			<ElTableColumn label="类型" width="140" align="center" v-if="props.rowType == 2">
				<template #default="scope">
					<ElFormItem :prop="`tableData[${scope.$index}].fileType`" :rules="rules.fileType">
						<ElSelect
							v-model="scope.row.fileType"
							placeholder="请选择类型"
							@change="handleFileTypeChange(scope.$index)"
						>
							<ElOption label="文件" :value="2" />
							<ElOption label="链接" :value="3" />
						</ElSelect>
					</ElFormItem>
				</template>
			</ElTableColumn>
			<!-- 资料内容 -->
			<ElTableColumn label="资料内容" min-width="200" align="center" v-if="props.rowType == 2">
				<template #default="scope">
					<template v-if="scope.row.fileType == 2">
						<ElFormItem
							:prop="`tableData[${scope.$index}].fileUrl`"
							class="item-box"
							:rules="rules.fileUrl"
						>
							<ElUpload
								:ref="(el) => setUploadRef(scope.$index, el)"
								action="/api/project/uploadFile"
								:on-success="handleUploadSuccess(scope.$index)"
								:on-error="handleUploadError"
								:headers="{ JsdcToken: appInstance.loginToken }"
								name="multipartFile"
								class="upload-box"
								:file-list="scope.row.uploadFileList || []"
								:auto-upload="true"
								:on-remove="handleRemove(scope.$index)"
								:limit="1"
								:on-exceed="handleExceed(scope.$index)"
								:on-change="handleFileChange(scope.$index)"
							>
								<ElButton type="primary">上传文件</ElButton>
							</ElUpload>
						</ElFormItem>
					</template>
					<template v-else>
						<ElFormItem :prop="`tableData[${scope.$index}].fileName`" :rules="rules.fileName">
							<ElInput v-model="scope.row.fileName" :suffix-icon="Link" placeholder="请输入链接" />
						</ElFormItem>
					</template>
				</template>
			</ElTableColumn>
		</ElTable>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, reactive } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import {
		ElTable,
		ElTableColumn,
		ElForm,
		ElFormItem,
		ElInput,
		ElTree,
		ElTreeSelect,
		ElRadioGroup,
		ElRadio,
		ElSelect,
		ElOption,
		ElUpload,
		ElButton,
		ElIcon,
		ElMessage,
		ElLink
	} from "element-plus";
	import { Plus, Link } from "@element-plus/icons-vue";
	import { appInstance } from "@/main";
	import { saveOrUpd } from "@/api/project/functionList";

	const props = defineProps({
		rowType: {
			type: String,
			default: ""
		},
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	// 初始化表格数据
	const tableData = ref<any[]>([]);

	const uploadRefs = ref<Array<InstanceType<typeof ElUpload>>>([]);

	// 定义设置 ref 的方法
	const setUploadRef = (index: number, el: InstanceType<typeof ElUpload> | null) => {
		if (index >= uploadRefs.value.length) {
			// 确保数组长度足够
			uploadRefs.value.length = index + 1;
		}
		uploadRefs.value[index] = el;
	};

	// 处理文件变化
	const handleFileChange = (index: number) => (file: any, fileList: any[]) => {
		// 确保只有一个文件
		if (fileList.length > 1) {
			fileList.splice(0, fileList.length - 1);
		}
		tableData.value[index].uploadFileList = fileList;
	};

	// 处理文件超出限制
	const handleExceed = (index: number) => async (files: any[], fileList: any[]) => {
		try {
			const uploadInstance = uploadRefs.value[index];
			if (!uploadInstance) {
				console.error(`未找到索引为 ${index} 的 ElUpload 实例`);
				return;
			}

			// 模拟删除原有文件
			if (fileList.length > 0) {
				const oldFile = fileList[0];
				// 调用 ElUpload 的 remove 方法删除文件
				uploadInstance.handleRemove(oldFile);
			}

			// 获取新文件
			const newFile = files[0];
			newFile.uid = Date.now();
			newFile.status = "ready";

			// 手动将新文件添加到文件列表
			uploadInstance.handleStart(newFile);

			// 手动触发上传
			if (typeof uploadInstance.submit === "function") {
				console.log("准备触发重新上传");
				await uploadInstance.submit();
				console.log("重新上传已触发");
			} else {
				console.error("ElUpload 实例缺少 submit 方法");
			}
		} catch (error) {
			console.error("重新上传时出错:", error);
			ElMessage.error("重新上传失败，请重试");
		}
	};

	// 删除文件处理函数
	const handleRemove = (index: number) => (file: any, fileList: any[]) => {
		tableData.value[index].uploadFileList = fileList;
		// 清除对应的文件信息
		tableData.value[index].contentType = "";
		tableData.value[index].fileMd5 = "";
		tableData.value[index].fileName = "";
		tableData.value[index].fileSize = "";
		tableData.value[index].fileUrl = "";
	};

	// 处理文件上传成功
	const handleUploadSuccess = (index: number) => (response: any, file: any, fileList: any) => {
		ElMessage.success("文件上传成功");
		Object.assign(tableData.value[index], {
			contentType: response.data.contentType,
			fileMd5: response.data.fileMd5,
			fileName: response.data.fileName,
			fileSize: response.data.fileSize,
			fileType: response.data.fileType == 1 ? 2 : response.data.fileType,
			fileUrl: response.data.fileUrl
		});
		tableData.value[index].uploadFileList = fileList; // 更新文件列表
	};
	// 处理文件上传失败
	const handleUploadError = () => {
		ElMessage.error("文件上传失败，请重试");
	};

	// 处理 fileType 切换
	const handleFileTypeChange = (index: number) => {
		if (tableData.value[index].fileType === 3) {
			// 切换到链接类型，清空文件上传列表
			tableData.value[index].uploadFileList = [];
			tableData.value[index].fileUrl = "";
			const uploadInstance = uploadRefs.value[index];
			if (uploadInstance) {
				uploadInstance.clearFiles();
			}
			tableData.value[index].fileName = "";
		}
	};

	const form = reactive({ tableData });
	const rules = reactive({
		dataFileName: [{ required: true, message: "请输入资料名称", trigger: "blur" }],
		fileType: [{ required: true, message: "请选择类型", trigger: "change" }],
		fileUrl: [{ required: true, message: "请上传文件", trigger: "change" }],
		fileName: [{ required: true, message: "请输入链接", trigger: "blur" }]
	});

	const { registerFormDone, formInstance } = useDialogForm();

	if (props.rowInfo.id) {
		// 编辑模式下，初始化表格数据
		const clonedRowInfo = JSON.parse(JSON.stringify(props.rowInfo));
		if (clonedRowInfo.fileType !== undefined) {
			clonedRowInfo.fileType = Number(clonedRowInfo.fileType);
		}
		// 初始化上传文件列表
		if (clonedRowInfo.fileUrl) {
			clonedRowInfo.uploadFileList = [
				{
					name: clonedRowInfo.fileName,
					url: clonedRowInfo.fileUrl,
					response: {
						data: clonedRowInfo
					}
				}
			];
		} else {
			clonedRowInfo.uploadFileList = [];
		}
		tableData.value[0] = clonedRowInfo;
	}

	registerFormDone(async () => {
		try {
			await formInstance.value?.validate();
			// 处理上传文件列表数据格式
			const dataToSave = tableData.value.map((item) => {
				const newItem = { ...item };
				if (newItem.uploadFileList && newItem.uploadFileList.length > 0) {
					console.log("newItem.uploadFileList", newItem.uploadFileList);
					newItem.fileUrl = newItem.uploadFileList[0].response.data.fileUrl;
					newItem.fileName = newItem.uploadFileList[0].name;
				}
				delete newItem.uploadFileList;
				return newItem;
			});
			await saveOrUpd(dataToSave);
			return true;
		} catch (error) {
			console.error("保存时出错:", error);
			ElMessage.error("保存失败，请检查输入信息");
			return false;
		}
	});
</script>
<style scoped lang="scss">
	.addNewBtn {
		border: 1px solid #efefef;
		border-left: 1.5px solid #efefef;
		text-align: center;
		padding: 6px;
	}
	:deep(.el-table__inner-wrapper:before) {
		height: 0;
	}
	:deep(.el-form-item) {
		margin-bottom: 0;
	}
	:deep(.item-box .el-form-item__error) {
		top: 10px;
	}
	.upload-box {
		width: 100%;
		display: flex;
		flex-direction: row-reverse;
		align-items: center;
		:deep(.el-upload) {
			margin-left: 8px;
		}
		:deep(.el-upload-list) {
			flex: 1;
			margin: 0;
			.el-upload-list__item {
				margin-bottom: 0;
				height: 32px;
				line-height: 32px;
			}
		}
	}
</style>
