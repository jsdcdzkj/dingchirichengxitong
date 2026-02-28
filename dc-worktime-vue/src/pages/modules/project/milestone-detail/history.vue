<template>
	<div style="overflow: auto">
		<ElCard shadow="never" class="m-t-10px">
			<div class="flex flex-items-center">
				<img w="24px" h="24px" src="../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
				<span class="m-l-10px">项目里程碑/阶段信息</span>
			</div>
			<div class="m-t-20px">
				<ElDescriptions :column="1">
					<ElDescriptionsItem label="迭代名称:">{{ detail.iterationName }}</ElDescriptionsItem>
					<ElDescriptionsItem label="阶段名称:">
						<el-link v-if="detail?.groupType == 1" link>需求阶段</el-link>
						<el-link v-else-if="detail.groupType == 2" link>设计阶段</el-link>
						<el-link v-else-if="detail.groupType == 3" link>开发阶段</el-link>
						<el-link v-else-if="detail.groupType == 4" link>内部评审阶段</el-link>
						<el-link v-else link>--</el-link>
					</ElDescriptionsItem>
					<ElDescriptionsItem label="里程碑阶段:">{{ detail.milestoneName }}</ElDescriptionsItem>
				</ElDescriptions>
			</div>
		</ElCard>
		<ElCard shadow="never" class="m-t-10px">
			<div class="flex flex-items-center">
				<img w="24px" h="24px" src="../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
				<span class="m-l-10px">评审信息</span>
			</div>
			<div class="m-t-20px">
				<ElDescriptions :column="1">
					<ElDescriptionsItem label="计划评审时间：">{{ detail.proReview.createTime }}</ElDescriptionsItem>
					<ElDescriptionsItem label="评审地点:">
						<span style="word-break: break-all">{{ detail.proReview.reviewPlace }}</span>
					</ElDescriptionsItem>
					<ElDescriptionsItem label="评审内容描述：">
						<span style="word-break: break-all">{{ detail.proReview.reviewDesc }}</span>
					</ElDescriptionsItem>
					<ElDescriptionsItem label="参会成员：" class="flex">
						<div class="inline-flex gap-10px">
							<span
								class="desc-item"
								:key="index"
								v-for="(item, index) in detail.proReview.participatingMemberNames.split(',')"
								>{{ item }}</span
							>
						</div>
					</ElDescriptionsItem>
				</ElDescriptions>
			</div>
		</ElCard>
		<ElCard shadow="never" class="m-t-10px">
			<div class="flex flex-items-center">
				<img w="24px" h="24px" src="../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
				<span class="m-l-10px">验收BUG记录</span>
			</div>
			<div class="m-t-20px">
				<ElTable border :data="detail.proReview.testBugs">
					<ElTableColumn label="ID" prop="id" width="80px" align="center"
						><template #default="scope">
							{{ scope.$index + 1 }}
						</template></ElTableColumn
					>
					<ElTableColumn label="板块" align="center" v-if="showBug">
						<template #default> 质量问题 </template>
					</ElTableColumn>
					<ElTableColumn label="问题类型" prop="question_type_name" align="center" v-if="!showBug">
					</ElTableColumn>
					<!-- question_type_name -->
					<ElTableColumn label="类型" align="center" v-if="showBug">
						<template #default="scope">
							<span v-if="scope.row.severity == 1">轻微bug</span>
							<span v-else-if="scope.row.severity == 2">一般bug</span>
							<span v-else-if="scope.row.severity == 3">重大bug</span>
						</template>
					</ElTableColumn>
					<ElTableColumn label="BUG标题" align="center">
						<template #default="scope">
							<div
								@click="handleBugDetail(scope.row)"
								class="desc-img"
								v-html="scope.row.bug_title"
							></div>
						</template>
					</ElTableColumn>
					<!-- <ElTableColumn label="责任人" align="center" v-if="showBug">
						<template #default="scope">
							<div>
								{{ scope.row.assigned_to_user_type[0] == 1 ? "前端" : "后端" }}
							</div>
						</template>
					</ElTableColumn> -->
					<ElTableColumn label="责任人" prop="assigned_to_user_name" align="center"> </ElTableColumn>
				</ElTable>
			</div>
		</ElCard>
		<ElCard shadow="never" class="m-t-10px">
			<div class="flex flex-items-center">
				<img w="24px" h="24px" src="../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
				<span class="m-l-10px">评审总结信息</span>
			</div>
			<div class="m-t-20px">
				<!-- 原型阶段 -->
				<PrototypeRate reviewComments v-if="detail.groupType == 1" :data="detail?.proReview" />
				<!-- 设计阶段 -->
				<DesignRate reviewComments v-else-if="detail.groupType == 2" :data="detail?.proReview" />
				<!-- 内部评审阶段 和 开发阶段-->
				<BugRate reviewComments v-else :data="detail?.proReview" />
			</div>
		</ElCard>
	</div>
</template>

<script lang="tsx" setup>
	import { appInstance } from "@/main";
	import { reactive, computed } from "vue";
	import { object } from "vue-types";
	import { createDrawerAsync } from "@/core/dialog";
	import { ElDescriptions, ElDescriptionsItem, ElTable, ElTableColumn, ElCard, ElButton } from "element-plus";
	import BugRate from "./components/bug-rate.vue";
	import DesignRate from "./components/design-rate.vue";
	import PrototypeRate from "./components/prototype-rate.vue";
	import DetailPage from "@/pages/modules/task/task-detail/index.vue";
	const data = reactive({
		data: [{ id: 1 }, { id: 2 }, { id: 3 }, { id: 4 }, { id: 5 }]
	});

	const props = defineProps({
		detail: object().def()
	});

	const showBug = computed(() => {
		return props.detail?.groupType != 1 && props.detail?.groupType != 2;
	});

	const handleBugDetail = (row) => {
		// appInstance?.router.push({
		// 	path: "/bug/detail",
		// 	query: {
		// 		id: row.id
		// 	}
		// });
		createDrawerAsync(
			{ title: row.bug_title, width: "80%", showNext: false, showConfirm: false },
			{},
			<DetailPage type={2} id={row.id} showBtn={false} />
		);
	};

	const objectSpanMethod = ({ row, column, rowIndex, columnIndex }) => {
		if (columnIndex === 1) {
			if (rowIndex % 5 == 0) {
				return {
					rowspan: 5,
					colspan: 1
				};
			} else {
				return {
					rowspan: 0,
					colspan: 0
				};
			}
		}
	};
</script>

<style lang="scss" scoped>
	.desc-item {
		background: #f8f8f8;
		border-radius: 4px 4px 4px 4px;
		height: 32px;
		line-height: 32px;
		padding-left: 10px;
		padding-right: 10px;
		text-align: center;
	}
	::v-deep .el-descriptions__cell {
		display: flex;
		align-items: center;
	}
	.desc-img {
		text-decoration: underline;
		color: cornflowerblue;
		cursor: pointer;
	}
</style>
