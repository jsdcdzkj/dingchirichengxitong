<template>
	<BasePage>
		<!-- base-search -->
		<template #pageSearch>
			<div
				style="
					background: var(--el-page-color);
					border-radius: 8px 8px 8px 8px;
					border: 1px solid rgba(0, 0, 0, 0.06);
					padding: 20px;
				"
			>
				<ElForm :model="form" labelWidth="100px" ref="formSearch" @submit.prevent>
					<ElRow :gutter="10">
						<ElCol :span="7">
							<ElFormItem label="资料内容">
								<ElInput
									placeholder="资料内容"
									v-model="fileName"
									@keyup.enter="onSearch"
									clearable
								></ElInput>
							</ElFormItem>
						</ElCol>
						<ElCol span="4">
							<ElButton :icon="Search" type="primary" @click="onSearch"> 搜索 </ElButton>
							<ElButton :icon="Refresh" @click="onReset"> 重置 </ElButton>
						</ElCol>
					</ElRow>
				</ElForm>
			</div>
		</template>

		<template #pageHeader>
			<TableHeader title="项目资料列表">
				<!-- <ElInput
					placeholder="搜索名称"
					:prefix-icon="Search"
					v-model:model-value="fileName"
					clearable
					@keyup.enter="debouncedFetchData"
					@clear="debouncedFetchData"
				></ElInput>
				<div class="num-des">已提交{{ notPay }}项，共{{ total }}项</div> -->
				<ElButton type="success" v-if="activeName == 12" @click="addDeliverable" :icon="Plus">新增</ElButton>
			</TableHeader>
		</template>

		<template #pageBody>
			<!-- 加tab选项卡 类型有 2:项目管理文件 3：交付物  8完成任务成果附件 9. 优化建议, 11.功能点附件  12.手动创建 -->
			<el-tabs v-model="activeName" @tab-click="handleClick">
				<el-tab-pane :name="2">
					<template #label>
						<span class="ml-10px">项目管理文件</span>
					</template>
				</el-tab-pane>
				<el-tab-pane :name="3">
					<template #label>
						<span class="ml-10px">交付物</span>
					</template>
				</el-tab-pane>
				<el-tab-pane :name="8">
					<template #label>
						<span class="ml-10px">完成任务成果附件</span>
					</template>
				</el-tab-pane>
				<el-tab-pane :name="9">
					<template #label>
						<span class="ml-10px">优化建议</span>
					</template>
				</el-tab-pane>
				<el-tab-pane :name="11">
					<template #label>
						<span class="ml-10px">功能点附件</span>
					</template>
				</el-tab-pane>
				<el-tab-pane :name="12">
					<template #label>
						<span class="ml-10px">手动创建</span>
					</template>
				</el-tab-pane>
				<div class="mt20px">
					<ElTable border :data="pageData.dataSource" stripe>
						<!-- 序号 -->
						<ElTableColumn label="序号" width="80" align="center">
							<template #default="scope">
								{{ (currentPage - 1) * pageSize + scope.$index + 1 }}
							</template>
						</ElTableColumn>
						<!-- 角色名称 -->
						<ElTableColumn label="资料名称" prop="dataFileName" align="center" show-overflow-tooltip>
							<template #default="scope">
								<div class="edit-text">
									<span class="mr6px maxlang">{{ scope.row.dataFileName }}</span>
									<template v-if="scope.row.bizType == 12">
										<!-- 编辑 删除行 -->
										<ElIcon @click="handleEdit(scope.row, '1')" class="iconE mr6px">
											<Edit />
										</ElIcon>
										<ElIcon @click="handleDelete(scope.row)" class="iconE">
											<Delete />
										</ElIcon>
									</template>
								</div>
							</template>
						</ElTableColumn>
						<ElTableColumn label="类型" prop="fileType" align="center" width="100">
							<template #default="scope">
								{{ scope.row.fileType == 3 ? "链接" : "文件" }}
							</template>
						</ElTableColumn>
						<ElTableColumn
							label="资料内容"
							prop="file"
							align="center"
							min-width="200"
							show-overflow-tooltip
						>
							<!-- 如果是文件，展示文件名称，并且添加下载功能 如果是链接，展示链接地址，并且添加跳转功能 -->

							<template #default="scope">
								<div class="edit-text">
									<div class="des-long">
										<template v-if="scope.row.fileType == 3">
											<span v-if="!scope.row.fileName || scope.row.fileName == '未交付'">无</span>
											<a
												v-else
												class="links"
												type="primary"
												:href="addProtocol(scope.row.fileName)"
												target="_blank"
											>
												{{ scope.row.fileName }}</a
											>
										</template>
										<!-- <template v-else-if="scope.row.fileType == 2"> -->
										<template v-else>
											<span
												v-if="scope.row.fileName"
												class="links"
												type="primary"
												@click="downFile(scope.row.fileUrl, scope.row.fileName)"
												>{{ scope.row.fileName }}</span
											>
											<span v-else>无</span>
										</template>
									</div>
									<template v-if="scope.row.bizType == 12">
										<!-- 编辑 删除行 -->
										<ElIcon @click="handleEdit(scope.row, '2')" class="iconE mr6px ml6px">
											<Edit />
										</ElIcon>
										<template v-if="scope.row.fileName">
											<ElIcon @click="handleDeleteFile(scope.row)" class="iconE">
												<Delete />
											</ElIcon>
										</template>
									</template>
								</div>
							</template>
						</ElTableColumn>
						<ElTableColumn label="文件大小" prop="fileSize" align="center" width="120">
							<template #default="scope">
								{{ scope.row.fileSize ? scope.row.fileSize : "--" }}
							</template>
						</ElTableColumn>
						<ElTableColumn
							label="提交时间"
							prop="uploadTime"
							align="center"
							width="180"
							sortable
						></ElTableColumn>
						<ElTableColumn label="交付者" prop="createUsername" align="center" width="90"></ElTableColumn>
						<ElTableColumn label="来源名称" prop="sourceName" align="center" show-overflow-tooltip>
							<template #default="scope">
								<span
									class="links"
									v-if="scope.row.bizType == 8"
									type="primary"
									@click="handleViewTask(1, scope.row.bizId, scope.row)"
									>{{ scope.row.sourceName }}</span
								>
								<span
									class="links"
									v-else-if="scope.row.bizType == 9"
									type="primary"
									@click="handleViewSuggest(scope.row)"
									>{{ scope.row.sourceName }}</span
								>
								<span
									class="links"
									v-else-if="scope.row.bizType == 11"
									type="primary"
									@click="handleViewFunction(scope.row)"
									>{{ scope.row.sourceName }}</span
								>
								<span v-else>{{ scope.row.sourceName }}</span>
							</template>
						</ElTableColumn>
					</ElTable>
					<!-- 分页 -->
					<div class="pagination-box">
						<ElPagination
							background
							layout="prev, pager, next, jumper, ->, total"
							:total="total"
							@current-change="handleCurrentChange"
							@size-change="handleSizeChange"
							:currentPage="currentPage"
							:pageSize="pageSize"
						></ElPagination>
					</div>
				</div>
			</el-tabs>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import TableHeader from "@/core/struct/table/table-header";
	import { reactive, ref } from "vue";
	import { appInstance } from "@/main";
	import { Search, Plus, Edit, Delete, Refresh } from "@element-plus/icons-vue";
	import { selectPageList, selectFileList, deleteById, deleteFileById } from "@/api/project/functionList";
	import { downFile } from "@/core/utils";
	import { debounce } from "lodash-es";
	import Detail from "./dialog/index.vue";
	import LineEdit from "./page/lineEdit.vue";
	import { ElMessage, ElPagination, ElTable, ElTableColumn, ElMessageBox } from "element-plus";
	import DetailPage from "../../task/task-detail/index.vue";
	import DetailPage2 from "../../optimization/suggestion/dialog/detailPage.vue";
	import DetailPage3 from "../function-list/dialog/detailPage.vue";

	const form = reactive({
		project_id: ""
	});
	// 初始化分页参数
	const currentPage = ref(1);
	const pageSize = ref(20);
	const total = ref(0);
	const notPay = ref(0);
	// 新增 sortDate 变量，初始值为 1 表示正序
	const sortDate = ref(2);

	const pageData = reactive({
		dataSource: [],
		currentPage: currentPage.value,
		pageSize: pageSize.value
	});
	const fileName = ref("");
	const activeName = ref(2);

	// 回车查询
	// const handleKeyDown = (e: KeyboardEvent) => {
	// 	if (e.key == "Enter") {
	// 		fetchData();
	// 	}
	// };
	// 获取数据的函数
	const fetchData = async () => {
		try {
			const response = await selectFileList({
				sortDate: sortDate.value,
				projectId: appInstance.projectItem.id,
				pageNo: currentPage.value,
				pageSize: pageSize.value,
				fileName: fileName.value,
				bizType: activeName.value
			});
			pageData.dataSource = response.records;
			total.value = response.total;
			notPay.value = response.total - response.notPay;
		} catch (error) {
			console.error("获取数据失败:", error);
		}
	};

	// 初始化数据
	fetchData();

	const handleClick = (tab: TabsPaneContext, event: Event) => {
		fetchData();
	};

	// 页码改变事件处理函数
	const handleCurrentChange = (page: number) => {
		currentPage.value = page;
		fetchData();
	};

	// 每页显示数量改变事件处理函数
	const handleSizeChange = (size: number) => {
		pageSize.value = size;
		currentPage.value = 1; // 重置页码为第一页
		fetchData();
	};
	// 排序改变事件处理函数
	const handleSortChange = (sort) => {
		if (sort.order === "ascending") {
			sortDate.value = 1; // 正序
		} else if (sort.order === "descending") {
			sortDate.value = 2; // 倒序
		} else {
			sortDate.value = 1; // 默认正序
		}
		// 重新获取数据
		fetchData();
	};
	const addProtocol = (url) => {
		if (!/^(https?:\/\/)/i.test(url)) {
			return `https://${url}`;
		}
		return url;
	};

	// 新增交付物
	const addDeliverable = () => {
		createModelAsync(
			{
				title: "提交资料",
				showNext: false,
				width: "1000px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<Detail />
		).then((res) => {
			fetchData();
		});
	};
	// 编辑交付物
	const handleEdit = (row, type) => {
		createModelAsync(
			{
				title: "编辑资料信息",
				showNext: false,
				width: "1000px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<LineEdit rowInfo={row} rowType={type} />
		).then((res) => {
			fetchData();
		});
	};
	// 删除交付物
	const handleDelete = (row: any) => {
		ElMessageBox.alert("是否删除该条数据", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					deleteById({ id: row.id }).then(() => {
						ElMessage.success("删除成功");
						fetchData();
					});
				}
			}
		});
	};
	// 删除交付物
	const handleDeleteFile = (row: any) => {
		ElMessageBox.alert("是否删除该条数据", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					deleteFileById({ id: row.id }).then(() => {
						ElMessage.success("删除成功");
						fetchData();
					});
				}
			}
		});
	};

	const onSearch = () => {
		fetchData();
	};
	const onReset = () => {
		fileName.value = "";
		fetchData();
	};
	const handleViewTask = (index: any, id: any, row) => {
		createDrawerAsync(
			{ title: row.sourceName, width: "1100px", showNext: false, showConfirm: false },
			{},
			<DetailPage type={index} id={id} />
		);
	};
	const handleViewSuggest = (row) => {
		createDrawerAsync(
			{ title: "详情", showNext: false, width: "1056px", showConfirm: false },
			{},
			<DetailPage2 rowId={row.bizId} />
		);
	};
	const handleViewFunction = (row) => {
		createDrawerAsync(
			{ title: "详情", showNext: false, width: "1056px", showConfirm: false },
			{},
			<DetailPage3 rowId={row.bizId} />
		);
	};
</script>
<style lang="scss" scoped>
	.page-search {
		height: 328px;
		width: 100%;
		background-color: var(--el-page-color);
		border-radius: 6px;
		padding: 32px;
		.title {
			position: absolute;
			top: 0;
			left: 0;
			right: 0;
			bottom: 36px;
			margin: auto;
			width: 200px;
			height: 20px;
			color: #fff;
		}
	}
	.num-des {
		flex: none;
		font-size: 14px;
	}
	.pagination-box {
		display: flex;
		justify-content: center;
		margin-top: 12px;
	}
	.edit-text {
		display: flex;
		align-items: center;
		justify-content: center;
		.maxlang {
			max-width: calc(100% - 60px);
			overflow: hidden;
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
		}
		.iconE {
			cursor: pointer;
			&:hover {
				color: #409eff;
			}
		}
		.des-long {
			display: inline-block;
			max-width: calc(100% - 60px);
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
			a {
				display: inline-block;
				width: 100%;
				white-space: nowrap;
				overflow: hidden;
				text-overflow: ellipsis;
				:deep(.el-link__inner) {
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
				}
			}
		}
	}
	:deep(.el-form-item) {
		margin-bottom: 0;
	}
	.links {
		color: #409eff;
		cursor: pointer;
		text-decoration: none;
		vertical-align: middle;
	}
</style>
