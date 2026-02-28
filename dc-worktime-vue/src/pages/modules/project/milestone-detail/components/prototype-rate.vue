<template>
	<ElDescriptions :column="2">
		<ElDescriptionsItem label="里程碑延期天数：">{{
			data.milestonePostponeDay == 0
				? "准时完成"
				: data.milestonePostponeDay < 0
					? `提前${Math.abs(data.milestonePostponeDay)}天`
					: `延期${data.milestonePostponeDay}天`
		}}</ElDescriptionsItem>
		<ElDescriptionsItem label="里程碑按期完成评分：" width="400px">
			<template #label>
				<ElPopover width="400px" placement="top">
					<div>超前完成:40;按时完成:30;轻微延期:20;严重延期:0;</div>

					<template #reference>
						<div class="flex items-center gap-10px">
							<ElIcon>
								<QuestionFilled></QuestionFilled>
							</ElIcon>
							<span> 里程碑按期完成评分： </span>
						</div>
					</template>
				</ElPopover>
			</template>
			{{ data.milestoneFinishPoint }}分
		</ElDescriptionsItem>
		<ElDescriptionsItem label="需求完整性问题数量">{{ data.requireFullQuestionNum }}</ElDescriptionsItem>
		<ElDescriptionsItem label="需求完整性扣分:">
			<template #label>
				<ElPopover width="400px" placement="top">
					<div>
						项目需求完整性（30）：较为完整，可用于开发。分数扣完为止，不负分<br />
						(1)需求覆盖度(10) <br />
						是否覆盖所有业务场景和异常流程,遗漏主要场景扣3分/项，次要场景扣1分/项 <br />
						(2)功能完善度（10）<br />
						需求拆解到开发可执行单元（如API级/页面级/功能点级），遗漏核心需求扣3分/项，非核心需求扣1分/项
						(3)逻辑一致性<br />
						逻辑冲突扣4分，目标偏离扣5分<br />
						(5)表述清晰度（5） 重要功能点描述清晰有效，无歧义，少一个扣1分
					</div>

					<template #reference>
						<div class="flex items-center gap-10px">
							<ElIcon>
								<QuestionFilled></QuestionFilled>
							</ElIcon>
							<span> 需求完整性扣分: </span>
						</div>
					</template>
				</ElPopover>
			</template>
			{{ data.requireFullQuestionDeduct }}分
		</ElDescriptionsItem>
		<ElDescriptionsItem label="原型设计问题数量">{{ data.uiDesignQuestionNum }}</ElDescriptionsItem>

		<ElDescriptionsItem label="原型设计扣分"
			><template #label>
				<ElPopover width="400px" placement="top">
					<div>
						原型设计评审（20）：业务功能和流程较完善，分数扣完为止，不负分<br />
						(1)业务流程完整性(10)<br />
						核心业务流程闭环，异常分支覆盖，核心功能缺失扣5分/项,非核心功能缺失扣1分/项<br />
						2）界面元素准确性(5)<br />
						控件类型准确（如按钮、表单、多选/单选下拉框），错误1个扣1分<br />
						3）交互逻辑合理性(5)<br />
						用户操作路径≤4步完成高频功能，无冗余跳转,路径复杂扣2分，逻辑死循环扣5分<br />
					</div>

					<template #reference>
						<div class="flex items-center gap-10px">
							<ElIcon>
								<QuestionFilled></QuestionFilled>
							</ElIcon>
							<span> 原型设计扣分: </span>
						</div>
					</template>
				</ElPopover>
			</template>
			{{ data.uiDesignQuestionDeduct }}分
		</ElDescriptionsItem>
		<ElDescriptionsItem label="用户体验问题数量">{{ data.userExperienceQuestionNum }}</ElDescriptionsItem>
		<ElDescriptionsItem label="用户体验扣分"
			><template #label>
				<ElPopover width="400px" placement="top">
					<div>
						3良好的用户体验（10）：原型交互设计友好，操作流畅，无明显的设计不合理之处。分数扣完为止，不负分<br />
						(1)信息层级清晰度(5)<br />
						主次信息区分明显（如标题放大、辅助文案弱化）,每出现一个问题扣1分<br />
						(2)反馈机制准确性(5)<br />
						重要/核心操作结果提示明确（成功/失败/等待），无提示扣1分/项<br />
					</div>

					<template #reference>
						<div class="flex items-center gap-10px">
							<ElIcon>
								<QuestionFilled></QuestionFilled>
							</ElIcon>
							<span> 用户体验扣分: </span>
						</div>
					</template>
				</ElPopover>
			</template>
			{{ data.userExperienceDeduct }}分
		</ElDescriptionsItem>
		<ElDescriptionsItem label="评审结果：">{{
			data.reviewTotalPoints >= 50 ? "通过" : "未通过"
		}}</ElDescriptionsItem>
		<ElDescriptionsItem label="评审总分：">
			<template #label>
				<ElPopover width="400px" placement="top">
					<div>
						1.评审总分=里程碑按期完成评分+60-需求完整性扣分-原型设计扣分-用户体验扣分<br />
						2.评审总分>=50分的,评审通过;评审总分小于50分的,评审不通过<br />
					</div>

					<template #reference>
						<div class="flex items-center gap-10px">
							<ElIcon>
								<QuestionFilled></QuestionFilled>
							</ElIcon>
							<span> 评审总分: </span>
						</div>
					</template>
				</ElPopover>
			</template>
			{{ data.reviewTotalPoints }}分
		</ElDescriptionsItem>
		<ElDescriptionsItem label="评审意见：" v-if="reviewComments">{{ data.reviewComments }}</ElDescriptionsItem>
	</ElDescriptions>
</template>

<script setup lang="ts">
	import { ElDescriptions, ElDescriptionsItem, ElIcon, ElPopover } from "element-plus";
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
