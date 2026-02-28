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
							<ElIcon><QuestionFilled></QuestionFilled></ElIcon>
							<span> 里程碑按期完成评分： </span>
						</div>
					</template>
				</ElPopover>
			</template>
			{{ data.milestoneFinishPoint }}分
		</ElDescriptionsItem>
		<ElDescriptionsItem label="功能实现与信息架构完整性数量">{{
			data.functionArchitectureFullQuestionNum
		}}</ElDescriptionsItem>
		<ElDescriptionsItem label="功能实现与信息架构完整性扣分:">
			<template #label>
				<ElPopover width="400px" placement="top">
					<div>
						1.功能实现与信息架构：较为清晰和完整，可用于开发（30）<br />
						(1)功能设计完整性(10)<br />
						是否覆盖所有原型所有功能和页面,是否完成原型标注的需要设计的交互动画，遗漏主要功能或未设计交互动画扣3分/项，次要功能扣1分/项<br />
						(2)数据展示合理性（10）<br />
						表格/图表信息密度合理，字段、信息、数据等展示行距、列宽、操作单元间距等是否合理，偏差1项扣1分<br />
						(3)非正常状态展示完整性(10)<br />
						根据原型文档标注，针对特殊功能提供的默认态、悬停态、点击态、禁用态等基础状态设计，重要功能页面覆盖初始/加载/空数据/异常状态（如无结果提示、错误占位图）图像，缺失1项扣一分<br />
					</div>

					<template #reference>
						<div class="flex items-center gap-10px">
							<ElIcon>
								<QuestionFilled></QuestionFilled>
							</ElIcon>
							<span> 功能实现与信息架构完整性扣分: </span>
						</div>
					</template>
				</ElPopover>
			</template>
			{{ data.functionArchitectureFullDeduct }}分
		</ElDescriptionsItem>
		<ElDescriptionsItem label="设计效果缺陷数量">{{ data.designDefectNum }}</ElDescriptionsItem>
		<ElDescriptionsItem label="设计效果缺陷扣分">
			<template #label>
				<ElPopover width="400px" placement="top">
					<div>
						2.设计效果评分：符合、贴切项目定位和使用单位要求（30）<br />
						(1)调性一致性(10)<br />
						主色、辅助色、字体、图标严格遵循规范和客户趋向要求，严重偏差扣5分/项，非严重偏差扣1分/项<br />
						(2)设计合理性(10)<br />
						信息间隔（间隔、分隔线等）符合要求，标题/正文/辅助文字通过字号或色阶等明确区分，图标风格一致（线性/面性/混合），每偏差一项扣1分<br />
						(3)设计规范性（10）<br />
						相同功能组件（按钮、输入框、弹窗）样式完全一致，栅格系统、元素对齐严格遵循设计标准，每偏差一项扣1分<br />
					</div>
					<template #reference>
						<div class="flex items-center gap-10px">
							<ElIcon>
								<QuestionFilled></QuestionFilled>
							</ElIcon>
							<span> 设计效果缺陷扣分: </span>
						</div>
					</template>
				</ElPopover>
			</template>
			{{ data.designDefectDeduct }}分
		</ElDescriptionsItem>
		<ElDescriptionsItem label="评审结果：">{{
			data.reviewTotalPoints >= 50 ? "通过" : "未通过"
		}}</ElDescriptionsItem>
		<ElDescriptionsItem label="评审总分：">
			<template #label>
				<ElPopover width="400px" placement="top">
					<div>
						1.评审总分=里程碑按期完成评分+60-功能实现与信息架构-设计效果评分<br />
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
