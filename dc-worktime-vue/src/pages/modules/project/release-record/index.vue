<template>
	<BasePage v-bind="page" :asideLeftWidth="460">
		<template #pageLeft>
			<ElCard shadow="never" style="border: none">
				<template #header>
					<div class="card-header flex flex-items-center flex-justify-between">
						<span class="man">发布详情</span>
						<div class="task-name flex flex-items-center">
							<div class="number">共 {{ total }} 条</div>
							<ElButton type="success" @click="handleRelease">
								<CirclePlus />
								新建发布
							</ElButton>
						</div>
					</div>
				</template>
				<div class="release-wrap">
					<template v-if="list.length > 0">
						<div
							class="release-list"
							v-for="(item, index) in list"
							:key="index"
							:class="{ active: activeIndex == index }"
							@click="handleDetail(index, item.id)"
						>
							<div class="flex flex-items-center flex-justify-between" style="padding: 20px 24px">
								<div class="left-info">
									<div class="title">
										<ElIcon class="icon-bg"><Promotion /></ElIcon>
										<span class="name">{{ item.title }}</span>
									</div>
									<div class="user">
										<span class="m-r-8px">{{ item.personOfChargeName }}</span>
										<span>{{ item.publishDate || item.predictPublishDate }}</span>
									</div>
								</div>
								<div class="status" :class="statusList[item.state]?.type">
									{{ statusList[item.state]?.name }}
								</div>
							</div>
							<div class="flex release-btn-wrap">
								<div
									v-if="item.state == 3 || item.state == 2"
									class="flex-1 release-btn"
									style="border-right: 1px solid rgba(0, 0, 0, 0.06)"
									@click.stop="handleDelete(item.id)"
								>
									删除
								</div>
								<div
									v-if="item.state == 3 || item.state == 2"
									class="flex-1 release-btn"
									style="border-right: 1px solid rgba(0, 0, 0, 0.06)"
									@click.stop="handleRelease(item)"
								>
									编辑
								</div>
								<div
									v-if="item.state == 3 || item.state == 2"
									class="flex-1 release-btn"
									@click.stop="checkRelease(item)"
								>
									发布审核
								</div>
								<div
									v-if="item.state == 4"
									class="flex-1 release-btn"
									@click.stop="confirmRelease(item)"
								>
									发布确认
								</div>
								<div
									v-if="item.state == 1"
									class="flex-1 release-btn"
									style="color: rgba(0, 0, 0, 0.25)"
								>
									已发布
								</div>
							</div>
						</div>
					</template>
					<div
						v-else
						class="flex flex-1 flex-items-center flex-col flex-justify-center"
						style="height: calc(100vh - 214px)"
					>
						<img src="@/assets/project/no-release.png" style="width: 220px" />
						<span style="margin-top: 10px; color: #999">暂无数据</span>
					</div>
				</div>
				<div class="pages">
					<ElPagination
						:current-page="pageNum"
						:page-size="pageSize"
						:total="total"
						layout="total, prev, pager, next"
						@current-change="handleSizeChange"
					/>
				</div>
			</ElCard>
		</template>
		<template #pageSearch>
			<div class="release-info">
				<div class="title">
					<ElIcon class="icon-bg"><Promotion /></ElIcon>
					<span>{{ detailInfo.title }}</span>
				</div>
				<div class="flex">
					<div class="task-item m-r-16px m-b-16px">
						<div class="flex flex-items-center">
							<div class="icon-bg">
								<span v-if="detailInfo.createUsername?.substring(0, 1)">{{ detailInfo.createUsername?.substring(0, 1) }}</span>
								<ElIcon v-else><UserFilled /></ElIcon>
							</div>
							
							<div>
								<div class="name">{{ detailInfo.createUsername || '--' }}</div>
								<div class="type">创建人</div>
							</div>
						</div>
					</div>
					<div class="task-item m-r-16px m-b-16px">
						<div class="flex flex-items-center">
							<div class="icon-bg">
								<span v-if="detailInfo.auditUsername?.substring(0, 1)">{{ detailInfo.auditUsername?.substring(0, 1)}}</span>
								<ElIcon v-else><UserFilled /></ElIcon>
							</div>
							<div>
								<div class="name">{{ detailInfo.auditUsername || '--' }}</div>
								<div class="type">审核人</div>
							</div>
						</div>
					</div>
					<div class="task-item m-r-16px m-b-16px">
						<div class="flex flex-items-center">
							<div class="icon-bg">
								<span v-if="detailInfo.realPublishUsername?.substring(0, 1)">{{ detailInfo.realPublishUsername?.substring(0, 1) }}</span>
								<ElIcon v-else><UserFilled /></ElIcon>
							</div>
							<div>
								<div class="name">{{ detailInfo.realPublishUsername || '--' }}</div>
								<div class="type">实际发布人</div>
							</div>
						</div>
					</div>
					<div class="task-item m-b-16px">
						<div class="flex flex-items-center">
							<div class="icon-bg">
								<ElIcon><Calendar /></ElIcon>
							</div>
							<div>
								<div class="name">{{ detailInfo.publishDate || detailInfo.predictPublishDate || '--' }}</div>
								<div class="type">发布时间</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</template>
		<template #pageBody>
			<!-- <ElCard shadow="never" style="border: none">
				<template #header>
					<div
						class="card-header flex flex-items-center flex-justify-between"
					>
						<span class="man">发布明细</span>
					</div>
				</template>
				<div style="text-align:left;padding: 16px">
					<div class="desc-img" v-html="detailInfo.description"></div>
				</div>
			</ElCard> -->
			<div class="release-content">
				<div class="release-detail">
					<ElTabs v-model="activeName">
						<ElTabPane name="first" label="发布创建信息">
							<div class="detail">
								<template v-if="detailInfo.description">
									<div class="name">
										<img
											style="width: 24px; height: 24px; margin-right: 8px"
											src="@/assets/task/tag-icon.png"
											alt=""
										/>
										描述
									</div>
									<div class="desc-img" v-html="detailInfo.description"></div>
								</template>
								<div
									v-else
									class="flex flex-1 flex-items-center flex-col flex-justify-center"
									style="height: 100%;width: 100%"
								>
									<img src="@/assets/no-data.png" style="width: 220px" />
									<span style="margin-top: 10px; color: #999">暂无数据</span>
								</div>
								
							</div>
						</ElTabPane>
						<ElTabPane name="second" label="发布审核信息">
							<div class="check-detail flex">
								<template v-if="detailInfo.publishAudits">
									<div class="flex-1 check-info" style="border-right: 1px solid rgba(0, 0, 0, 0.06)">
									<div
										class="check-list flex flex-items-center"
										v-for="(item, index) in detailInfo.publishAudits"
										:key="index"
										:class="{ activeInfo: activeCheckIndex == index }"
										@click="handleCheckDetail(index, item)"
									>
										<div class="flex flex-items-center">
											<div class="icon-bg">
												{{ item.auditUsername?.substring(0, 1) }}
											</div>
											<div>
												<div class="name">{{ item.auditUsername }}</div>
												<div class="type">{{ item.auditDate }}</div>
											</div>
										</div>
										<ElTag v-if="item.auditStatus == 1" type="success" plain round>审核通过</ElTag>
										<ElTag v-if="item.auditStatus == 2" type="danger" plain round>审核驳回</ElTag>
									</div>
								</div>
								<div class="flex-1 check-info" style="border-right: 1px solid rgba(0, 0, 0, 0.06)">
									<div class="check-info-detail">
										<div class="name">
											<img
												style="width: 24px; height: 24px; margin-right: 8px"
												src="@/assets/task/tag-icon.png"
												alt=""
											/>
											审核意见
										</div>
										<div class="desc-img" v-html="detailInfo.publishAudits[activeCheckIndex]?.auditOpinion"></div>
									</div>
								</div>
								<div class="flex-1 check-info m-l-24px">
									<div class="title m-b-16px">发布基础</div>
									<ElDescriptions :column="1">
										<ElDescriptionsItem label="指定发布人：">{{
											detailInfo.appointPublishUsername || "--"
										}}</ElDescriptionsItem>
										<ElDescriptionsItem label="预计发布时间：">{{
											detailInfo.predictPublishDate || "--"
										}}</ElDescriptionsItem>
									</ElDescriptions>
									<div class="title m-b-16px">发布描述</div>
									<div class="desc-img" v-html="detailInfo.description"></div>
								</div>
								</template>
								<div
									v-else
									class="flex flex-1 flex-items-center flex-col flex-justify-center"
									style="height: 100%;width: 100%"
								>
									<img src="@/assets/no-data.png" style="width: 220px" />
									<span style="margin-top: 10px; color: #999">暂无数据</span>
								</div>
							</div>
							
						</ElTabPane>
						<ElTabPane name="third" label="发布确认信息">
							<div class="detail">
								<template v-if="detailInfo.verifyDescription">
									<div class="name">
									<img
										style="width: 24px; height: 24px; margin-right: 8px"
										src="@/assets/task/tag-icon.png"
										alt=""
									/>
									描述
								</div>
								<div class="desc-img" v-html="detailInfo.verifyDescription"></div>
								</template>
								<div
									v-else
									class="flex flex-1 flex-items-center flex-col flex-justify-center"
									style="height: 100%;width: 100%"
								>
									<img src="@/assets/no-data.png" style="width: 220px" />
									<span style="margin-top: 10px; color: #999">暂无数据</span>
								</div>
							</div>
						</ElTabPane>
					</ElTabs>
				</div>
				<div class="detail-info">
					<ElDescriptions :column="3">
						<ElDescriptionsItem label="创建时间：">{{ detailInfo.createTime || "--" }}</ElDescriptionsItem>
						<ElDescriptionsItem label="审核通过时间：">{{
							detailInfo.checkTime || "--"
						}}</ElDescriptionsItem>
						<ElDescriptionsItem label="发布完成时间：">{{
							detailInfo.publishDate || "--"
						}}</ElDescriptionsItem>
						<ElDescriptionsItem label="指定发布人：">{{
							detailInfo.appointPublishUsername || "--"
						}}</ElDescriptionsItem>
						<ElDescriptionsItem label="实际发布人：">{{ detailInfo.realPublishUsername || '--' }}</ElDescriptionsItem>
					</ElDescriptions>
				</div>
			</div>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
import { ref, onMounted } from "vue";
import { createModelAsync, createDrawerAsync } from "@/core/dialog";
import { type FormConfig } from "@/core/struct/form/use-base-form";
import { usePage } from "@/core/struct/page";
import BasePage from "@/core/struct/page/base-page";
import AddRelease from "./dialog/add-release.vue";
import CheckRelease from "./dialog/check-release.vue";
import ConfirmRelease from "./dialog/confirm-release.vue";
import { ElMessageBox, ElPagination } from "element-plus";
import { CirclePlus, Calendar, Promotion, UserFilled } from "@element-plus/icons-vue";
import { deleteById, selectPageList, getEntityById, auditPublish } from "@/api/project/release-record";

const statusList = [
	{},
	{
		name: "已发布",
		type: "lightGreen"
	},
	{
		name: "审核驳回",
		type: "red"
	},
	{
		name: "待审核",
		type: "green"
	},
	{
		name: "未发布",
		type: "orange"
	}
];

// 新建发布
const handleRelease = (item) => {
	createDrawerAsync(
		{
			title: item.id ? "编辑" : "新建发布",
			showNext: false,
			width: "800px",
			closeOnClickModal: false,
			closeOnPressEscape: false
		},
		{},
		<AddRelease id={item.id} />
	).then(() => {
		pageNum.value = 1;
		onSearch({});
	});
};

// 发布审核
const checkRelease = (item) => {
	createModelAsync(
		{ title: "发布审核", showNext: false, width: "800px", closeOnClickModal: false, closeOnPressEscape: false },
		{},
		<CheckRelease id={item.id} />
	).then(() => {
		onSearch({});
	});
};

// 发布确认
const confirmRelease = (item) => {
	createModelAsync(
		{ title: "发布确认", showNext: false, width: "800px", closeOnClickModal: false, closeOnPressEscape: false },
		{},
		<ConfirmRelease id={item.id} />
	).then(() => {
		onSearch({});
	});
};

// 删除
const handleDelete = (id) => {
	ElMessageBox.alert("是否删除该条数据", "提示", {
		confirmButtonText: "确认",
		callback: (action: Action) => {
			if (action == "confirm") {
				deleteById(id).then(() => {
					pageNum.value = 1;
					onSearch({});
				});
			}
		}
	});
};

const { page, pageApi } = usePage({
	title: "发布明细",
	listApi: async () => {}
});
const loading = ref(false);
const list = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const onSearch = async (data) => {
	loading.value = true;
	try {
		const res = await selectPageList({
			pageSize: pageSize.value,
			pageNo: pageNum.value,
			sortDate: 2,
			projectId: appInstance.projectItem.id
		});
		console.log("44444444444444res", res);
		list.value = res.records || [];
		total.value = res.total;
		detailInfo.value = res.records[activeIndex.value] || {};
		const time = detailInfo.value?.publishAudits.filter(val => val.auditStatus == 1)
		detailInfo.value.checkTime = time[0]?.auditDate
		detailInfo.value.auditUsername = time[0]?.auditUsername
		return res;
	} finally {
		loading.value = false;
	}
};

// 分页
const handleSizeChange = (val) => {
	pageNum.value = val;
	onSearch({});
};

onMounted(() => {
	onSearch({});
});

// 点击详情
const detailInfo = ref({});
const activeIndex = ref(0);
const handleDetail = (index, id) => {
	activeIndex.value = index;
	activeCheckIndex.value = 0;
	getEntityById(id).then((res) => {
		detailInfo.value = res || {};
		const time = detailInfo.value?.publishAudits.filter(val => val.auditStatus == 1)
		detailInfo.value.checkTime = time[0]?.auditDate
		detailInfo.value.auditUsername = time[0]?.auditUsername
	});
};

// 点击审核的列表
const detailCheckInfo = ref({});
const activeCheckIndex = ref(0);
const handleCheckDetail = (index, item) => {
	activeCheckIndex.value = index;
	detailCheckInfo.value = item
}

// 审核
const handleCommand = (val) => {
	auditPublish({ id: detailInfo.value.id, publishState: val }).then((res) => {
		onSearch({});
	});
};

// 发布详情
const activeName = ref("first");
</script>
<style scoped lang="scss">
.release-wrap {
	padding: 24px;
	height: calc(100vh - 230px);
	overflow:auto;
	.release-list {
		border-radius: 0px 0px 0px 0px;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.65);
		line-height: 22px;
		border: 1px solid rgba(0, 0, 0, 0.06);
		margin-bottom: 16px;
		border-radius: 8px;

		.left-info {
			text-align: left;
			flex:1;
			max-width: 80%;
		}

		.status {
			font-size: 14px;
			color: #40bfa8;
			line-height: 28px;
			padding: 0 10px;
			height: 28px;
			background: #ecf9f6;
			border-radius: 24px 24px 24px 24px;
		}

		.green {
			color: #40bfa8;
			background: #ecf9f6;
		}
		.red {
			color: #f56c6c;
			background: #fef0f0;
		}
		.lightGreen {
			color: #67c23a;
			background: #f0f9eb;
		}

		.orange {
			color: #f5a250;
			background: #fef6ed;
		}

		.icon-bg {
			width: 28px;
			height: 24px;
			background: #1b86ff;
			border-radius: 3px 3px 3px 3px;
			color: #fff;
			margin-right: 8px;
		}

		.title {
			font-size: 14px;
			color: #1b86ff;
			line-height: 22px;
			display: flex;
			margin-bottom: 12px;
			align-items: center;
			.name {
				overflow: hidden;
				white-space: nowrap;
				text-overflow: ellipsis;
				width: 72%;
				display: inline-block;
			}
		}

		.release-btn-wrap {
		}
		.release-btn {
			height: 44px;
			background: rgba(255, 255, 255, 0);
			font-size: 14px;
			color: rgba(0, 0, 0, 0.85);
			line-height: 44px;
			border-top: 1px solid rgba(0, 0, 0, 0.06);
		}
	}

	.active {
		background: rgba(27, 134, 255, 0.04);
		border: 2px solid #499eff;
	}
}

.pages {
	display: flex;
	height: 64px;
	align-items: center;
	justify-content: flex-end;
}
.number {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.85);
	line-height: 22px;
	margin-right: 16px;
}
.release-info {
	padding: 24px 32px;
	text-align: left;
	background: #fff;
	border-radius: 8px;

	.title {
		font-size: 14px;
		color: rgba(0, 0, 0, 0.65);
		line-height: 22px;
		text-align: left;
		margin-bottom: 24px;
		text-align: left;
	}
	.icon-bg {
		width: 28px;
		height: 24px;
		background: #1b86ff;
		border-radius: 3px 3px 3px 3px;
		color: #fff;
		margin-bottom: 0;
		margin-right: 8px;
	}
	.task-item {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: space-between;
		height: 70px;
		background: #ffffff;
		box-shadow: 0px 0px 12px 0px rgba(230, 237, 241, 0.5);
		border-radius: 8px 8px 8px 8px;
		border: 1px solid rgba(0, 0, 0, 0.06);
		padding: 14px 16px;

		.icon-bg {
			width: 36px;
			height: 36px;
			line-height: 36px;
			background: #1b86ff;
			border-radius: 77px 77px 77px 77px;
			margin-right: 12px;
			color: #fff;
			text-align: center;
		}

		.name {
			font-size: 14px;
			color: rgba(0, 0, 0, 0.85);
			line-height: 22px;
			height: 22px;
			font-weight: bold;
		}

		.type {
			font-size: 12px;
			color: rgba(0, 0, 0, 0.65);
			line-height: 20px;
		}
	}
}

.release-content {
	background: #f8f8f8;
	position: relative;
	top: -10px;
	left: -10px;
	right: -10px;
	bottom: -10px;
	width: calc(100% + 20px);
	height: calc(100%);

	.release-detail {
		background: #fff;
		height: calc(100% - 116px);
		margin-bottom: 16px;
	}

	.detail-info {
		padding: 32px;
		background: #fff;
	}
}

.detail {
	display: flex;
	flex-direction: column;
	align-items: flex-start;
	padding: 16px 32px;
	height: calc(100vh - 510px);
	overflow: auto;

	.name {
		display: flex;
		align-items: center;
	}

	.desc-img {
			text-align: left;
		}
}

.check-detail {
	padding: 16px 32px;
	height: calc(100vh - 510px);
	overflow: auto;
	.check-info {
		max-width: 34%;
		height: 100%;
		overflow: auto;

		.check-info-detail {
			display: flex;
			flex-direction: column;
			align-items: flex-start;
			margin: 0 24px;

			.name {
				display: flex;
				align-items: center;
			}
		}

		.desc-img {
			text-align: left;
		}
	}

	.check-list {
		height: 80px;
		background: #FFFFFF;
		justify-content: space-between;
		padding: 0 16px;
		border-radius: 8px 8px 8px 8px;
		border: 1px solid rgba(0,0,0,0.06);
		margin-bottom: 16px;
		margin-right: 24px;
		.title {
			font-size: 16px;
			color: rgba(0, 0, 0, 0.85);
			line-height: 24px;
			text-align: left;
			margin-bottom: 16px;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			-webkit-line-clamp: 2;
			overflow: hidden;
		}

		.icon-bg {
			width: 32px;
			height: 32px;
			line-height: 32px;
			background: #1b86ff;
			border-radius: 77px 77px 77px 77px;
			margin-right: 12px;
			color: #fff;
		}

		.name {
			font-size: 14px;
			color: rgba(0, 0, 0, 0.85);
			line-height: 22px;
			height: 22px;
			text-overflow: ellipsis;
			overflow: hidden;
			white-space: nowrap;
			max-width: 160px;
			text-align: left;
			font-weight: bold;
			margin-bottom: 6px;
		}

		.type {
			font-size: 12px;
			color: rgba(0, 0, 0, 0.65);
			line-height: 20px;
		}
	}

	.activeInfo {
		background: rgba(27, 134, 255, 0.04);
		position: relative;
		overflow: hidden;
		&::before {
			content: '';
			width: 2px;
			height: 80px;
			background: #1B86FF;
			position: absolute;
			top:0;
			left:0;
			z-index:1;
		}
	}

	.title {
		width: 100%;
		height: 24px;
		line-height: 24px;
		background: linear-gradient(90deg, rgba(27, 134, 255, 0.1) 0%, rgba(27, 134, 255, 0) 100%);
		border-radius: 0px 0px 0px 0px;
		border-left: 4px solid #1b86ff;
		font-size: 16px;
		font-weight: bold;
		padding-left: 10px;
		text-align: left;
	}
}

::v-deep {
	.el-card__body {
		padding: 0;
	}
	.el-tabs--bottom > .el-tabs__header .el-tabs__item:nth-child(2),
	.el-tabs--top > .el-tabs__header .el-tabs__item:nth-child(2) {
		padding-left: 20px;
	}
	.el-tabs__item {
		height: 56px;
	}
}
</style>
