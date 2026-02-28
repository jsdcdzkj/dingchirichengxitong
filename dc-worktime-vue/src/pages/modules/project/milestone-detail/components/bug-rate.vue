<template>
	<ElDescriptions :column="2">
		<ElDescriptionsItem label="良好的用户体验评分:" v-if="reviewComments"
			><ElRate
				show-score
				text-color="#1B86FF"
				:colors="['#1B86FF', '#1B86FF', '#1B86FF']"
				score-template="{value} 分"
				disabled
				v-model="data.userExperiencePoint"
		/></ElDescriptionsItem>
		<ElDescriptionsItem label="逻辑清晰评分:" v-if="reviewComments"
			><ElRate
				show-score
				text-color="#1B86FF"
				disabled
				v-model="data.clearLogicPoint"
				:colors="['#1B86FF', '#1B86FF', '#1B86FF']"
				score-template="{value} 分"
		/></ElDescriptionsItem>
		<ElDescriptionsItem label="里程碑延期天数：">
			{{
				data.milestonePostponeDay == 0
					? "准时完成"
					: data.milestonePostponeDay < 0
						? `提前${Math.abs(data.milestonePostponeDay)}天`
						: `延期${data.milestonePostponeDay}天`
			}}
		</ElDescriptionsItem>
		<ElDescriptionsItem label="里程碑按期完成评分：" width="400px">
			<template #label>
				<ElPopover width="400px" placement="top">
					<div>超前完成:40;按时完成:30;轻微延期:20;严重延期:0;</div>

					<template #reference>
						<div class="flex items-center gap-10px">
							<ElIcon><QuestionFilled></QuestionFilled></ElIcon>
							<span> 里程碑按期完成评分： </span>
						</div>
					</template>
				</ElPopover>
			</template>
			{{ data.milestoneFinishPoint }}分
		</ElDescriptionsItem>
		<ElDescriptionsItem label="质量问题数量">{{ data.qualityProblemNum }}</ElDescriptionsItem>
		<ElDescriptionsItem label="质量问题扣分:"
			><template #label>
				<ElPopover width="400px" placement="top">
					<div>
						严重BUG一个扣5分<br />
						一般BUG一个扣2分<br />
						轻微BUG一个扣1分<br />
						出现阻塞性BUG扣50分<br />
					</div>

					<template #reference>
						<div class="flex items-center gap-10px">
							<ElIcon><QuestionFilled></QuestionFilled></ElIcon>
							<span> 质量问题扣分: </span>
						</div>
					</template>
				</ElPopover>
			</template>
			{{ data.qualityProblemDeduct }}分
		</ElDescriptionsItem>
		<ElDescriptionsItem label="评审结果：">{{
			data.reviewTotalPoints >= 50 ? "通过" : "未通过"
		}}</ElDescriptionsItem>
		<ElDescriptionsItem label="评审总分："
			><template #label>
				<ElPopover width="400px" placement="top">
					<div>
						1.评审总分=里程碑按期完成评分+50-质量问题扣分+其他指标评价得分<br />
						2.评审总分>=50分的，评审通过；评审总分<50分的，评审不通过<br />
					</div>

					<template #reference>
						<div class="flex items-center gap-10px">
							<ElIcon><QuestionFilled></QuestionFilled></ElIcon>
							<span> 评审总分: </span>
						</div>
					</template>
				</ElPopover>
			</template>
			{{ data.reviewTotalPoints }}分</ElDescriptionsItem
		>
		<ElDescriptionsItem label="评审意见：" v-if="reviewComments">{{ data.reviewComments }}</ElDescriptionsItem>
	</ElDescriptions>
</template>

<script setup lang="ts">
	import { ElDescriptions, ElDescriptionsItem, ElIcon, ElPopover, ElRate } from "element-plus";
	import { QuestionFilled } from "@element-plus/icons-vue";
	import { object, bool } from "vue-types";
	import type { FinalRateType } from "../rate";
	const props = defineProps({
		data: object<FinalRateType>().def(),
		reviewComments: bool().def(false)
	});
</script>

<style scoped lang="scss">
	::v-deep .el-descriptions__cell {
		display: flex;
		width: 50%;
		flex: 1;
	}
	::v-deep .el-descriptions__table tr {
		display: flex;

		width: 100%;
	}
</style>
