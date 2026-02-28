<template>
	<div class="tabPage">
		<div class="project_tabs">
			<div class="flex1 flex overflow-auto">
				<div class="tabs flex" v-for="(el, index) in projectList" :key="index"
					:class="{ active: paramsData.pro_project_id == el.id }" @click="changeProject(el.id)">
					<img class="normalImg" src="@/assets/statistics/project_default.png" alt="" />
					<img class="activeImg" src="@/assets/statistics/project_active.png" alt="" />
					{{ el.projectName }}
				</div>
			</div>
			<el-button type="primary" @click="exportFile" :loading="exportLoading">导出全部项目工时核算表</el-button>
		</div>
		<div class="table_box flex w-100%">
			<div class="type_list" v-show="paramsData.pro_project_id != '-1'">
				<div class="line" :class="'line' + activeIndex"></div>
				<div class="type_item" :class="{ active: activeIndex === 1 }" @click="changeType(1)">
					<div class="item flex">
						<div class="img_box">
							<img src="@/assets/statistics/product_default.png" alt="" class="normarImg" />
							<img src="@/assets/statistics/product_active.png" alt="" class="activeImg" />
						</div>
						<div class="flex1">
							<div class="label">产品与项目</div>
						</div>
					</div>
					<div class="choose">
						<el-icon color="#1B86FF" size="20">
							<CaretLeft />
						</el-icon>
					</div>
				</div>
				<div class="type_item" :class="{ active: activeIndex === 2 }" @click="changeType(2)">
					<div class="item flex">
						<div class="img_box">
							<img src="@/assets/statistics/develop_default.png" alt="" class="normarImg" />
							<img src="@/assets/statistics/develop_active.png" alt="" class="activeImg" />
						</div>
						<div class="flex1">
							<div class="label">开发与测试</div>
						</div>
					</div>
					<div class="choose">
						<el-icon color="#1B86FF" size="20">
							<CaretLeft />
						</el-icon>
					</div>
				</div>
				<div class="type_item" :class="{ active: activeIndex === 3 }" @click="changeType(3)">
					<div class="item flex">
						<div class="img_box">
							<img src="@/assets/statistics/operations_default.png" alt="" class="normarImg" />
							<img src="@/assets/statistics/operations_active.png" alt="" class="activeImg" />
						</div>
						<div class="flex1">
							<div class="label">运维与实施</div>
						</div>
					</div>
					<div class="choose">
						<el-icon color="#1B86FF" size="20">
							<CaretLeft />
						</el-icon>
					</div>
				</div>
				<div class="type_item" :class="{ active: activeIndex === 4 }" @click="changeType(4)">
					<div class="item flex">
						<div class="img_box">
							<img src="@/assets/statistics/bug_default.png" alt="" class="normarImg" />
							<img src="@/assets/statistics/bug_active.png" alt="" class="activeImg" />
						</div>
						<div class="flex1">
							<div class="label">BUG修复</div>
						</div>
					</div>
					<div class="choose">
						<el-icon color="#1B86FF" size="20">
							<CaretLeft />
						</el-icon>
					</div>
				</div>
			</div>
			<div class="flex-1">
				<el-table :data="list" style="width: 100%" border :span-method="arraySpanMethod">
					<el-table-column type="index" label="序号" align="center" width="80">
						<template #default="{ $index }">
							{{ (paramsData.pageNo - 1) * paramsData.pageSize + $index + 1 }}
						</template>
					</el-table-column>
					<template v-if="paramsData.pro_project_id == '-1'">
						<el-table-column prop="title" label="任务名称" align="center" />
						<el-table-column prop="proposer_user_name" label="任务提出人" align="center" width="120" />
						<el-table-column prop="assigned_to_user_name" label="任务责任人" align="center" width="100" />
						<el-table-column prop="statusName" label="完成情况" align="center" width="100" />
						<el-table-column prop="start_time" label="开始时间" align="center" width="180" />
						<el-table-column prop="completion_time" label="完成时间" align="center" width="180" />
					</template>
					<template v-else-if="activeIndex === 1">
						<el-table-column prop="iteration_name" label="版本-阶段" align="center" />
						<el-table-column prop="plan_title" label="来源计划" align="center" />
						<el-table-column prop="title" label="任务名称" align="center" />
						<el-table-column prop="task_attribute_name" label="任务类型" align="center" width="120" />
						<el-table-column prop="realName" label="任务责任人" align="center" width="100" />
						<el-table-column prop="statusName" label="完成情况" align="center" width="100" />
						<el-table-column prop="start_time" label="开始时间" align="center" width="180" />
						<el-table-column prop="completion_time" label="完成时间" align="center" width="180" />
					</template>
					<template v-else-if="activeIndex === 2">
						<el-table-column prop="title" label="任务名称" align="center" />
						<el-table-column prop="task_attribute_name" label="任务类型" align="center" width="120" />
						<el-table-column prop="moduleName" label="目录-菜单" align="center" />
						<el-table-column prop="feature_name" label="功能名称" align="center" />
						<el-table-column prop="realName" label="任务责任人" align="center" width="100" />
						<el-table-column prop="statusName" label="完成情况" align="center" width="100" />
						<el-table-column prop="start_time" label="开始时间" align="center" width="180" />
						<el-table-column prop="completion_time" label="完成时间" align="center" width="180" />
					</template>
					<template v-else-if="activeIndex === 3">
						<el-table-column prop="title" label="任务名称" align="center" />
						<el-table-column prop="implementTypeName" label="任务类型" align="center" width="120" />
						<el-table-column prop="realName" label="任务责任人" align="center" width="100" />
						<el-table-column prop="statusName" label="完成情况" align="center" width="100" />
						<el-table-column prop="start_time" label="开始时间" align="center" width="180" />
						<el-table-column prop="completion_time" label="完成时间" align="center" width="180" />
					</template>
					<template v-else-if="activeIndex === 4">
						<el-table-column prop="bug_title" label="BUG名称" align="center" />
						<el-table-column prop="severityName" label="BUG严重程度" align="center" width="120" />
						<el-table-column prop="moduleName" label="目录-菜单" align="center" />
						<el-table-column prop="featureName" label="功能名称" align="center" />
						<el-table-column prop="assigned_to_user_name" label="任务责任人" align="center" width="100" />
						<el-table-column prop="statusName" label="完成情况" align="center" width="100" />
						<el-table-column prop="start_time" label="开始时间" align="center" width="180" />
						<el-table-column prop="completion_time" label="完成时间" align="center" width="180" />
					</template>
				</el-table>
				<div class="table_footer">
					<el-pagination background :total="total" :page-size="paramsData.pageSize"
						:page-sizes="[10, 20, 30, 50, 100]" :current-page="paramsData.pageNo"
						layout="sizes, prev, pager, next, jumper,total" @current-change="pageNoChange"
						@size-change="pageSizeChange"></el-pagination>
				</div>
			</div>
		</div>
	</div>
</template>
<script setup>
import { ref, defineProps, nextTick, watch } from "vue";
import { CaretLeft } from "@element-plus/icons-vue";
import { downFile } from "@/core/utils";
import {
	projectBugExport,
	projectDevelopExport,
	projectBugList,
	projectDevelopList,
	projectOperationExport,
	projectOperationList,
	projectProductExport,
	projectProductList,
	projectLeaderList,
	projectLeaderExport
} from "@/api/statistics/statistics";
import { ElMessage } from "element-plus";
const props = defineProps({
	projectList: {
		type: Array,
		default: () => []
	},
	startDate: {
		type: String,
		default: ""
	},
	endDate: {
		type: String,
		default: ""
	}
});
watch(
	() => props.projectList,
	(val) => {
		if (val.length > 0) {
			paramsData.value.pro_project_id = val[0].id;
			getList();
		} else {
			paramsData.value.pro_project_id = "";
			list.value = [];
		}
	}
);
watch(
	() => props.startDate,
	(val) => {
		paramsData.value.startDate = val;
	}
);
watch(
	() => props.endDate,
	(val) => {
		paramsData.value.endDate = val;
	}
);
const paramsData = ref({
	pro_project_id: "",
	pageNo: 1,
	pageSize: 20,
	startDate: props.startDate,
	endDate: props.endDate
});
const total = ref(0);
const list = ref([]);
const activeIndex = ref(1);
const exportLoading = ref(false);
const changeType = (index) => {
	if (index === activeIndex.value) return;
	activeIndex.value = index;
	paramsData.value.pageNo = 1;
	getList();
};
const getList = (fc) => {
	if (!paramsData.value.pro_project_id) {
		return;
	}
	if (paramsData.value.pro_project_id == "-1") {
		projectLeaderList(paramsData.value).then((res) => {
			list.value = res.records;
			total.value = res.total;
			fc && fc();
		});
		return;
	}
	switch (activeIndex.value) {
		case 1:
			projectProductList(paramsData.value).then((res) => {
				list.value = res.records;
				total.value = res.total;
				fc && fc();
			});
			break;
		case 2:
			projectDevelopList(paramsData.value).then((res) => {
				list.value = res.records;
				total.value = res.total;
				fc && fc();
			});
			break;
		case 3:
			projectOperationList(paramsData.value).then((res) => {
				list.value = res.records;
				total.value = res.total;
				fc && fc();
			});
			break;
		case 4:
			projectBugList(paramsData.value).then((res) => {
				list.value = res.records;
				total.value = res.total;
				fc && fc();
			});
			break;
	}
};
const arraySpanMethod = ({ row, column, rowIndex, columnIndex }) => {
	if ([3, 4].includes(activeIndex.value) || paramsData.value.pro_project_id == "-1") {
		return [1, 1];
	} else {
		if ([1, 2, 3].includes(columnIndex)) {
			if (columnIndex === 3 && activeIndex.value === 1) {
				return [1, 1];
			}
			if (rowIndex !== 0 && row[column.property] === list.value[rowIndex - 1][column.property]) {
				return [0, 1];
			}
			let lastIndex = "";
			let count = 0;
			list.value.forEach((item, i) => {
				if (i >= rowIndex && item[column.property] === row[column.property]) {
					if (lastIndex === "") {
						count++;
						lastIndex = i;
					} else if (lastIndex + 1 === i) {
						count++;
						lastIndex = i;
					}
				}
			});
			return [count, 1];
		} else {
			return [1, 1];
		}
	}
};
const changeProject = (id) => {
	paramsData.value.pro_project_id = id;
	paramsData.value.pageNo = 1;
	getList();
};
const pageNoChange = (val) => {
	paramsData.value.pageNo = val;
	getList(() => {
		nextTick(() => {
			document.getElementsByClassName("body-container")[0].scrollTop =
				document.getElementsByClassName("table_box")[0].getBoundingClientRect().y +
				document.getElementsByClassName("body-container")[0].scrollTop -
				40;
		});
	});
};
const pageSizeChange = (val) => {
	paramsData.value.pageSize = val;
	getList();
};
const exportFile = () => {
	if (!paramsData.value.pro_project_id) {
		ElMessage.error("请先选择项目");
		return;
	}
	exportLoading.value = true;
	if (paramsData.value.pro_project_id == "-1") {
		projectLeaderExport({ startDate: paramsData.value.startDate, endDate: paramsData.value.endDate }).then(
			(res) => {
				downFile(URL.createObjectURL(res), "项目工时核算表.xlsx");
				exportLoading.value = false;
				ElMessage.success("导出成功，正在下载...");
			}
		);
		return;
	}
	switch (activeIndex.value) {
		case 1:
			projectProductExport({
				startDate: paramsData.value.startDate,
				endDate: paramsData.value.endDate,
				pro_project_id: paramsData.value.pro_project_id
			}).then((res) => {
				downFile(URL.createObjectURL(res), "产品与项目.xlsx");
				exportLoading.value = false;
				ElMessage.success("导出成功，正在下载...");
			});
			break;
		case 2:
			projectDevelopExport({
				startDate: paramsData.value.startDate,
				endDate: paramsData.value.endDate,
				pro_project_id: paramsData.value.pro_project_id
			}).then((res) => {
				downFile(URL.createObjectURL(res), "开发与测试.xlsx");
				exportLoading.value = false;
				ElMessage.success("导出成功，正在下载...");
			});
			break;
		case 3:
			projectOperationExport({
				startDate: paramsData.value.startDate,
				endDate: paramsData.value.endDate,
				pro_project_id: paramsData.value.pro_project_id
			}).then((res) => {
				downFile(URL.createObjectURL(res), "运维与实施.xlsx");
				exportLoading.value = false;
				ElMessage.success("导出成功，正在下载...");
			});
			break;
		case 4:
			projectBugExport({
				startDate: paramsData.value.startDate,
				endDate: paramsData.value.endDate,
				pro_project_id: paramsData.value.pro_project_id
			}).then((res) => {
				downFile(URL.createObjectURL(res), "BUG修复.xlsx");
				exportLoading.value = false;
				ElMessage.success("导出成功，正在下载...");
			});
			break;
	}
};
</script>
<style scoped lang="scss">
.tabPage {
	background-color: #fff;

	.project_tabs {
		justify-content: space-between;
		align-items: center;
		display: flex;
		height: 56px;
		border-bottom: 1px solid rgba(0, 0, 0, 0.06);

		.tabs {
			height: 56px;
			text-align: center;
			line-height: 56px;
			font-size: 14px;
			color: rgba(0, 0, 0, 0.65);
			padding: 0 24px;
			cursor: pointer;
			align-items: center;
			flex-shrink: 0;

			img {
				width: 18px;
				height: 18px;
				margin-right: 4px;
			}

			.activeImg {
				display: none;
			}

			&.active {
				color: #1b86ff;
				border-bottom: 2px solid #1b86ff;

				.activeImg {
					display: block;
				}

				.normalImg {
					display: none;
				}
			}
		}
	}

	.overflow-auto {
		overflow: auto;
	}

	.table_box {
		padding: 24px;
	}

	.type_list {
		position: relative;
		width: 240px;
		padding-right: 4px;
		margin-right: 24px;
		flex-shrink: 0;

		.line {
			position: absolute;
			width: 4px;
			height: 100%;
			right: 0;
			top: 0;
			background: linear-gradient(180deg,
					rgba(27, 134, 255, 0.12) 0%,
					#1b86ff 12.5%,
					rgba(27, 134, 255, 0.12) 100%);
			z-index: 1;
			border-radius: 2px;

			&.line2 {
				background: linear-gradient(180deg,
						rgba(27, 134, 255, 0.12) 0%,
						#1b86ff 37.5%,
						rgba(27, 134, 255, 0.12) 100%);
			}

			&.line3 {
				background: linear-gradient(180deg,
						rgba(27, 134, 255, 0.12) 0%,
						#1b86ff 62.5%,
						rgba(27, 134, 255, 0.12) 100%);
			}

			&.line4 {
				background: linear-gradient(180deg,
						rgba(27, 134, 255, 0.12) 0%,
						#1b86ff 87.5%,
						rgba(27, 134, 255, 0.12) 100%);
			}
		}

		.type_item {
			.item {
				width: 206px;
				overflow: hidden;
				border-radius: 12px;
				border: 1px solid rgba(0, 0, 0, 0.06);
				align-items: center;
				cursor: pointer;
				margin-bottom: 8px;
				box-sizing: border-box;

				img {
					width: 40px;
					height: 40px;
				}

				.img_box {
					padding: 16px;
					background: rgba(80, 160, 255, 0.1);
					margin-right: 16px;

					.activeImg {
						display: none;
					}
				}

				.num {
					color: #275798;
					font-size: 18px;
					font-weight: bold;
				}

				.label {
					color: rgba(0, 0, 0, 0.65);
					font-size: 14px;
					margin-top: 4px;
				}
			}

			.choose {
				display: none;
			}

			&.active {
				position: relative;

				.item {
					background: url("@/assets/statistics/type_bg.png") no-repeat;
					background-size: 100% 100%;
					border: none;
				}

				.img_box {
					background: none;

					.activeImg {
						display: block;
					}

					.normarImg {
						display: none;
					}
				}

				.num {
					color: #fff;
				}

				.label {
					color: rgba(255, 255, 255, 0.85);
				}

				.choose {
					display: block;
					position: absolute;
					top: 50%;
					right: -8px;
					transform: translateY(-50%);
					z-index: 2;
				}
			}
		}
	}

	.table_footer {
		margin-top: 20px;
		display: flex;
		justify-content: center;
	}
}
</style>
