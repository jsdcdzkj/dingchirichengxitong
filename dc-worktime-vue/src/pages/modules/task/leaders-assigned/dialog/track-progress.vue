<template>
	<div class="progress-wrap">
		<div class="title">{{ detailInfo.title }}</div>
		<div class="flex flex-items-center m-b-24px m-t-24px">
			<div class="task-info flex flex-items-center m-r-12px">
				<div class="round">{{ detailInfo.assigned_to_user_name?.substring(0, 1) }}</div>
				<div class="info">
					<div class="val">{{ detailInfo.assigned_to_user_name || '--' }}</div>
					<div>负责人</div>
				</div>
			</div>
			<div class="task-info flex flex-items-center m-r-12px">
				<ElIcon class="m-r-8px round orange"><Right color="#fff" size="16px" /></ElIcon>
				<div class="info">
					<div class="val">{{ detailInfo.statusName || '--' }}</div>
					<div>当前状态</div>
				</div>
			</div>
			<div class="task-info flex flex-items-center">
				<ElIcon class="m-r-8px round white"><MoreFilled color="rgba(0,0,0,0.65)" size="16px" /></ElIcon>
				<div class="info">
					<div class="val">{{ detailInfo.urgencyName || '--' }}</div>
					<div>优先级</div>
				</div>
			</div>
		</div>
		<div style="height: 8px; background: #f8f8f8"></div>
		<div class="custom-header">
			<div class="title m-t-16px m-b-16px">任务明细</div>
			<ElTabs v-model="activeName">
				<ElTabPane name="first">
					<template #label>
						<span class="custom-tabs-label"> 详情 </span>
					</template>
					<div class="content m-t-16px">
						<div class="desc">
							<div class="name">
								<img
									style="width: 24px; height: 24px; margin-right: 8px"
									src="@/assets/task/tag-icon.png"
									alt=""
								/>
								描述
							</div>
							<div class="desc-img" v-html="detailInfo.description"></div>
						</div>
						<div class="desc m-t-16px">
							<div class="name">
								<img
									style="width: 24px; height: 24px; margin-right: 8px"
									src="@/assets/task/tag-icon.png"
									alt=""
								/>
								属性
							</div>
							<ElDescriptions :column="2" width="150px" style="width: 100%; margin-top: 24px">
								<ElDescriptionsItem label="截止日期：">
									{{ detailInfo.end_time }}
								</ElDescriptionsItem>
								<ElDescriptionsItem label="负责人：">
									{{ detailInfo.assigned_to_user_name }}
								</ElDescriptionsItem>
								<ElDescriptionsItem label="优先级：">
									{{ detailInfo.urgencyName }}
								</ElDescriptionsItem>
								<ElDescriptionsItem label="状态：">
									<ElTag class="m-r-10px" type="warning">{{ detailInfo.statusName || "--" }}</ElTag>
								</ElDescriptionsItem>
								<ElDescriptionsItem label="所属迭代：">
									{{ detailInfo.plan || "--" }}
								</ElDescriptionsItem>
							</ElDescriptions>
						</div>
					</div>
				</ElTabPane>
				<ElTabPane name="second">
					<template #label>
						<span class="custom-tabs-label"> 工时 </span>
					</template>
					<div class="flex task-card">
						<ElTable border :data="detailInfo.taskStopList">
							<ElTableColumn label="序号" width="80px" type="index" align="center"></ElTableColumn>
							<ElTableColumn label="开始时间" align="center">
								<template #default="scope"> {{ scope.row.start_time }} </template>
							</ElTableColumn>
							<ElTableColumn label="暂停/结束时间" align="center">
								<template #default="scope"> {{ scope.row.stop_time }} </template>
							</ElTableColumn>
							<ElTableColumn label="期间工时" align="center"
								><template #default="scope">
									{{ scope.row.workHours }}
								</template></ElTableColumn
							>
							<ElTableColumn label="累计已用工时" align="center"
								><template #default="scope">
									{{ scope.row.totalWorkHours }}
								</template></ElTableColumn
							>
						</ElTable>
					</div>
				</ElTabPane>
				<ElTabPane name="third">
					<template #label>
						<span class="custom-tabs-label"> 阶段性成果展示 </span>
					</template>
					<template v-if="detailInfo.taskLeadersResultsList && detailInfo.taskLeadersResultsList.length > 0">
						<div class="flex task-card">
						<div class="left">
							<div
								class="item"
								:class="{ active: activeIndex == index }"
								v-for="(item, index) in detailInfo.taskLeadersResultsList"
								:key="index"
								@click="handleClick(index, item)"
							>
								<span class="m-l-12px">{{ item.createUserName + "：" + item.createTime }}</span>
							</div>
						</div>
						<div class="right flex-1" v-if="resultInfo">
							<div class="m-b-16px desc-img" style="text-align: left" v-html="resultInfo.content"></div>
							<ElDescriptions :column="1">
								<ElDescriptionsItem label="任务附件：">
									<div style="display: inline-block; width: calc(100% - 100px); vertical-align: top">
										<div
											v-for="(itemFile, index) in resultInfo.fileList"
											:key="index"
											class="file-item flex flex-justify-between m-b-12px"
										>
											<ElLink class="flex flex-items-center"  @click="downFile(itemFile.fileUrl, itemFile.fileName)">
												<img
													src="@/assets/task/file-icon.png"
													style="
														width: 13px;
														height: 13px;
														margin-left: 17px;
														margin-right: 14px;
													"
												/>
												<span class="textOverflow">{{ itemFile.fileName }}</span>
											</ElLink>
										</div>
									</div>
								</ElDescriptionsItem>
							</ElDescriptions>
						</div>
					</div>
					</template>
					<div v-else class="flex flex-1 flex-items-center flex-col flex-justify-center" style="height:300px">
						<img src="@/assets/no-Data.png" style="width: 120px;" />
						<span style="margin-top: 10px;color: #999">暂无数据</span>
					</div>
				</ElTabPane>
			</ElTabs>
		</div>
	</div>
</template>

<script setup lang="ts">
import { getEntityLeader } from "@/api/task/leaders-assigned";
import { Right, MoreFilled } from "@element-plus/icons-vue";
import { ElTabPane, ElTabs, ElDescriptions, ElTable, ElTableColumn, ElDescriptionsItem, ElCard, ElLink } from "element-plus";
import { number } from "vue-types";
import { downFile } from "@/core/utils";
import { ref } from "vue";

const props = defineProps({
	id: number().def()
});

const activeName = ref("third");
const detailInfo = ref({});
if (props.id) {
	getEntityLeader(props.id).then((res) => {
		detailInfo.value = res;
		resultInfo.value = detailInfo.value.taskLeadersResultsList?.[0]
	});
}

const resultInfo = ref({})
const activeIndex = ref(0)
const handleClick = (index:any, item:any) => {
	activeIndex.value = index
	resultInfo.value = item
}
</script>
<style lang="scss" scoped>
.title {
	font-weight: bold;
	font-size: 16px;
	color: rgba(0, 0, 0, 0.85);
	line-height: 24px;
}
.progress-wrap {
	.round {
		width: 44px;
		height: 44px;
		color: #fff;
		background: #1b86ff;
		border-radius: 77px 77px 77px 77px;
		display: flex;
		justify-content: center;
		align-items: center;
		margin-left: 16px;
		margin-right: 12px;
	}

	.orange {
		background: #f5a250;
	}

	.white {
		background: #f8f8f8;
	}

	.task-info {
		flex: 1;
		height: 82px;
		background: #ffffff;
		box-shadow: 0px 0px 12px 0px rgba(230, 237, 241, 0.5);
		border-radius: 8px 8px 8px 8px;
		border: 1px solid rgba(0, 0, 0, 0.06);

		.info {
			font-size: 14px;
			color: rgba(0,0,0,0.65);
			.val {
				height: 24px;
				line-height: 24px;
				font-size: 16px;
				font-weight: bold;
				margin-bottom: 4px;
				color: rgba(0,0,0,0.85);
			}

		}
	}
}
.task-card {
	width: 100%;
	margin-top: 16px;
}
.content {
	.desc {
		background: #ffffff;
		border-radius: 8px 8px 8px 8px;
		border: 1px solid rgba(0, 0, 0, 0.06);
		padding: 24px;

		.name {
			display: flex;
			align-items: center;
		}
	}
}
.left {
	border-right: 1px solid rgba(0,0,0,0.06);
	margin-right: 24px;
	padding: 0 24px;
	.item {
		height: 44px;
		line-height: 44px;
		background: rgba(255,255,255,0);
		border-radius: 8px 8px 8px 8px;
		padding-right: 16px;
	}
	.active {
		background: #E8F3FF;
		border-radius: 8px 8px 8px 8px;
	}
}
.file-item {
	width: 100%;
	height: 40px;
	background: #F8F8F8;
	border-radius: 4px 4px 4px 4px;
	padding: 0 16px;
}
.textOverflow {
	white-space: nowrap;
	overflow:hidden;
	text-overflow: ellipsis;
}
</style>