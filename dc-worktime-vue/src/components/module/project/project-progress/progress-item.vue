<template>
	<div class="project-progress">
		<ElPopover width="850px" placement="top" v-model:visible="item.visible" :offset="10" trigger="hover">
			<el-descriptions :title="item.milestoneName" :column="3" border>
				<el-descriptions-item
					label="评审结果"
					label-align="right"
					align="center"
					label-class-name="my-label"
					class-name="my-content"
					width="150px"
					><template v-if="item.proReview">
						<ElTag disable-transitions type="success" v-if="Number(item.proReview.reviewTotalPoints) >= 60"
							>通过</ElTag
						>
						<ElTag disable-transitions type="warning" v-else>未通过</ElTag>
					</template>
					<span v-else>--</span></el-descriptions-item
				>
				<el-descriptions-item label="评审分数" label-align="right" align="center">
					<template v-if="item.proReview">
						<span> {{ item.proReview.reviewTotalPoints }} </span>
					</template>
					<span v-else>--</span>
				</el-descriptions-item>
				<el-descriptions-item label="延期情况" label-align="right" align="center">
					<template v-if="item.proReview">
						<span v-if="Number(item.proReview.milestonePostponeDay) > 0"> 延期完成 </span>
						<span v-else-if="Number(item.proReview.milestonePostponeDay) < 0"> 提前完成 </span>
						<span v-else>正常完成</span>
					</template>
					<span v-else>--</span>
				</el-descriptions-item>
				<el-descriptions-item label="关联计划或计划组" label-align="right" align="center">{{
					item.planName
				}}</el-descriptions-item>
				<el-descriptions-item label="约定结束日期" label-align="right" align="center">
					{{ item.appointFinishTime }}
				</el-descriptions-item>
				<el-descriptions-item label="实际结束时间" label-align="right" align="center">{{
					item.actualFinishTime
				}}</el-descriptions-item>
			</el-descriptions>
			<template #reference>
				<div class="prgress-t">
					<img class="progress-icon" w="72px" h="72px" :src="`/milestone/${icons[item.status]}`" />
					<div class="progress-node" :style="`background-color:${colors[item.status]}`"></div>
					<div class="progress-label">{{ item.milestoneName }}</div>
					<div class="progress-time">{{ item.appointFinishTime }}</div>
				</div>
			</template>
		</ElPopover>
		<div class="progress-p" :style="`width: ${item.w}px;background-color:${colors[item.status]}`"></div>
	</div>
</template>

<script setup lang="ts">
	import { number, object, string } from "vue-types";

	const icons = [
		"map-pin-2-fill-4.png",
		"map-pin-2-fill.png",
		"map-pin-2-fill-1.png",
		"map-pin-2-fill-2.png",
		"map-pin-2-fill-3.png",
		"map-pin-2-fill-3.png"
	];

	const colors = ["#F4F4F4", "#66C537", "#FF9030", "#F5585D", "#E0368D", "red"];

	defineProps({
		item: object<any>(),
		label: string().def("label"),
		time: string().def("time"),
		status: number().def(0)
	});
</script>

<style lang="scss" scoped>
	.progress-label {
		font-size: 18px;
		font-weight: bold;
		position: absolute;
		right: -74px;
		top: 116px;
		width: 148px;
		white-space: nowrap;
		text-overflow: ellipsis;
		overflow: hidden;

		z-index: 999;
	}

	.progress-time {
		font-weight: normal;
		font-size: 14px;
		position: absolute;
		top: 146px;
		right: -60px;

		z-index: 999;
		width: 120px;
	}

	.prgress-t {
		position: absolute;
		right: 0;
		height: 169px;
	}

	.progress-name {
		display: inline-block;
		max-width: 190px;
		text-overflow: ellipsis;
		overflow: hidden;
		white-space: nowrap;
	}
	.project-progress {
		display: flex;
		flex-direction: column;
		gap: 21px;
		position: relative;
		cursor: pointer;
		padding-bottom: 75px;

		.progress-p {
			height: 12px;
			margin-top: 82px;
		}
		.progress-icon {
			position: absolute;
			right: -36px;
			top: -10px;
		}
		.progress-node {
			border: 4px solid #ffffff;
			width: 20px;
			height: 20px;
			border-radius: 50%;
			position: absolute;
			top: 78px;
			right: -10px;
			z-index: 89;
		}
	}
</style>
